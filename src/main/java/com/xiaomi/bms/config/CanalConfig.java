package com.xiaomi.bms.config;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry.*;
import com.alibaba.otter.canal.protocol.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaomi.bms.constant.RedisConstant;
import com.xiaomi.bms.entity.WarnRule;
import com.xiaomi.bms.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
/**
 * @author zhuzhe1018
 * @date 2024/6/16
 */
@Configuration
public class CanalConfig {

    private static final Logger logger = LoggerFactory.getLogger(CanalConfig.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RedisUtil redisUtil;

    public CanalConfig(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    /**
     * 配置并返回一个CanalConnector实例。
     *
     * @return CanalConnector 连接到Canal服务器并设置订阅。
     */
    @Bean
    public CanalConnector canalConnector() {
        CanalConnector connector = CanalConnectors.newSingleConnector(
                new InetSocketAddress("127.0.0.1", 11111),
                "example",
                "",
                ""
        );
        connector.connect();
        connector.subscribe(".*\\.warn_rule");
        connector.rollback();
        return connector;
    }

    /**
     * 启动一个新的线程来监听Canal服务器上的变更。
     */
    @Bean
    public void listen() {
        new Thread(() -> {
            CanalConnector connector = canalConnector();
            while (true) {
                try {
                    int batchSize = 1000;
                    Message message = connector.getWithoutAck(batchSize);
                    long batchId = message.getId();
                    int size = message.getEntries().size();

                    if (batchId != -1 && size > 0) {
                        processEntries(message.getEntries());
                    }

                    connector.ack(batchId);
                } catch (Exception e) {
                    logger.error("处理Canal消息时出错", e);
                }
            }
        }).start();
    }

    /**
     * 处理Canal传递的所有Entry对象。
     *
     * @param entries Canal传递的Entry列表。
     */
    private void processEntries(List<Entry> entries) {
        entries.stream()
                .filter(entry -> entry.getEntryType() == EntryType.ROWDATA)
                .forEach(entry -> {
                    try {
                        RowChange rowChange = RowChange.parseFrom(entry.getStoreValue());
                        handleRowChange(entry, rowChange);
                    } catch (Exception e) {
                        logger.error("解析事件数据时出错: {}", entry.toString(), e);
                    }
                });
    }

    /**
     * 处理单个Entry的RowChange对象。
     *
     * @param entry    Canal传递的Entry对象。
     * @param rowChange 解析后的RowChange对象。
     */
    private void handleRowChange(Entry entry, RowChange rowChange) {
        String schemaName = entry.getHeader().getSchemaName();
        String tableName = entry.getHeader().getTableName();
        EventType eventType = rowChange.getEventType();

        logger.info("Binlog: {} , Schema: {} , Table: {} , EventType : {}",
                entry.getHeader().getLogfileName(),
                schemaName,
                tableName,
                eventType);

        rowChange.getRowDatasList().forEach(rowData -> {
            try {
                updateRedisCache(eventType, rowData.getBeforeColumnsList(), rowData.getAfterColumnsList());
            } catch (JsonProcessingException e) {
                logger.error("更新Redis缓存时出错", e);
            }
        });
    }

    /**
     * 更新Redis缓存，根据不同的事件类型（INSERT、UPDATE、DELETE）处理数据。
     *
     * @param eventType    事件类型。
     * @param beforeColumns 更新前的列数据。
     * @param afterColumns  更新后的列数据。
     * @throws JsonProcessingException JSON处理异常。
     */
    private void updateRedisCache(EventType eventType, List<Column> beforeColumns, List<Column> afterColumns) throws JsonProcessingException {
        WarnRule beWarnRule = parseToWarnRule(beforeColumns);
        WarnRule afWarnRule = parseToWarnRule(afterColumns);

        String batteryId = beWarnRule.getBatteryId();
        if (eventType == EventType.DELETE) {
            handleDeleteEvent(batteryId, beWarnRule);
        } else if (eventType == EventType.INSERT) {
            handleInsertEvent(batteryId, afWarnRule);
        } else if (eventType == EventType.UPDATE) {
            handleUpdateEvent(beWarnRule, afWarnRule);
        }
    }

    /**
     * 处理DELETE事件，更新Redis缓存。
     *
     * @param batteryId  电池ID。
     * @param beWarnRule 更新前的预警规则。
     * @throws JsonProcessingException JSON处理异常。
     */
    private void handleDeleteEvent(String batteryId, WarnRule beWarnRule) throws JsonProcessingException {
        if (redisUtil.hasKey(batteryId)) {
            List<WarnRule> warnRulesList = getWarnRulesList(batteryId);
            warnRulesList.removeIf(warnRule -> Objects.equals(warnRule.getId(), beWarnRule.getId()));
            updateRedisCache(batteryId, warnRulesList);
        }
    }

    /**
     * 处理INSERT事件，更新Redis缓存。
     *
     * @param batteryId  电池ID。
     * @param afWarnRule 更新后的预警规则。
     * @throws JsonProcessingException JSON处理异常。
     */
    private void handleInsertEvent(String batteryId, WarnRule afWarnRule) throws JsonProcessingException {
        if (redisUtil.hasKey(batteryId)) {
            List<WarnRule> warnRulesList = getWarnRulesList(batteryId);
            warnRulesList.add(afWarnRule);
            updateRedisCache(batteryId, warnRulesList);
        }
    }

    /**
     * 处理UPDATE事件，更新Redis缓存。
     *
     * @param beWarnRule 更新前的预警规则。
     * @param afWarnRule 更新后的预警规则。
     * @throws JsonProcessingException JSON处理异常。
     */
    private void handleUpdateEvent(WarnRule beWarnRule, WarnRule afWarnRule) throws JsonProcessingException {
        String beBatteryId = beWarnRule.getBatteryId();
        String afBatteryId = afWarnRule.getBatteryId();
        if (beBatteryId.equals(afBatteryId)) {
            if (redisUtil.hasKey(afBatteryId)) {
                List<WarnRule> warnRulesList = getWarnRulesList(beBatteryId);
                for (ListIterator<WarnRule> iterator = warnRulesList.listIterator(); iterator.hasNext(); ) {
                    WarnRule warnRule = iterator.next();
                    if (warnRule.getId().equals(afWarnRule.getId())) {
                        iterator.set(afWarnRule);
                    }
                }
                updateRedisCache(beBatteryId, warnRulesList);
            }
        } else {
            handleDeleteEvent(beBatteryId, beWarnRule);
            handleInsertEvent(afBatteryId, afWarnRule);
        }
    }

    /**
     * 从Redis中获取电池的预警规则列表。
     *
     * @param batteryId 电池ID。
     * @return warnRulesList 预警规则列表。
     * @throws JsonProcessingException JSON处理异常。
     */
    private List<WarnRule> getWarnRulesList(String batteryId) throws JsonProcessingException {
        String json = redisUtil.getString(batteryId);
        return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, WarnRule.class));
    }

    /**
     * 更新Redis缓存。
     *
     * @param batteryId      电池ID。
     * @param warnRulesList  预警规则列表。
     * @throws JsonProcessingException JSON处理异常。
     */
    private void updateRedisCache(String batteryId, List<WarnRule> warnRulesList) throws JsonProcessingException {
        redisUtil.setWithExpiration(batteryId, objectMapper.writeValueAsString(warnRulesList), RedisConstant.WARN_RULE_EXPIRE_TIME, RedisConstant.WARN_RULE_EXPIRE_UNIT);
    }

    /**
     * 将列数据解析为WarnRule对象。
     *
     * @param columnsList 列数据列表。
     * @return WarnRule 解析后的WarnRule对象。
     */
    private WarnRule parseToWarnRule(List<Column> columnsList) {
        WarnRule warnRule = new WarnRule();
        for (Column column : columnsList) {
            if ("id".equals(column.getName())) {
                warnRule.setId(column.getValue());
            }
            if ("rule_id".equals(column.getName())) {
                warnRule.setRuleId(column.getValue());
            }
            if ("battery_id".equals(column.getName())) {
                warnRule.setBatteryId(column.getValue());
            }
            if ("lower_limit".equals(column.getName())) {
                if (column.hasValue()) {
                    warnRule.setLowerLimit(new BigDecimal(column.getValue()));
                }
            }
            if ("upper_limit".equals(column.getName())) {
                if (column.hasValue()) {
                    warnRule.setUpperLimit(new BigDecimal(column.getValue()));
                }
            }
            if ("warn_level".equals(column.getName())) {
                warnRule.setWarnLevel(Integer.valueOf(column.getValue()));
            }
            if ("rule_name".equals(column.getName())) {
                warnRule.setRuleName(column.getValue());
            }
        }
        return warnRule;
    }
}
