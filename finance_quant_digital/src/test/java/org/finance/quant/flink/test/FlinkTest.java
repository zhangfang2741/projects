package org.finance.quant.flink.test;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class FlinkTest {
    @Test
    public void envTest() throws Exception {
        //构建环境
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        //通过字符串构建数据集
        DataSource<String> text = env.fromElements(
                "Who's there?",
                "I think I hear them Stand, ho! Who's there?"
        );
        //分割字符串,按照key进行分组,统计相同的key个数
        DataSet<Tuple2<String, Integer>> wordCounts = text
                .flatMap(new LineSplitter())
                .groupBy(0)//聚合
                .sum(1);//统计
        //打印
        wordCounts.print();
    }
    @Test
    public void timeWindowTest() throws Exception {
        //1.获取执行环境配置信息
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //2.定义加载或创建数据源（source）,监听9000端口的socket消息
        DataStream<String> textStream = env.socketTextStream("127.0.0.1", 9000, "\n");
        //3.
        DataStream<Tuple2<String, Integer>> result = textStream
                //map是将每一行单词变为一个tuple2
                .map(line -> Tuple2.of(line.trim(), 1))
                //如果要用Lambda表示是，Tuple2是泛型，那就得用returns指定类型。
                .returns(Types.TUPLE(Types.STRING, Types.INT))
                //keyBy进行分区，按照第一列，也就是按照单词进行分区
                .keyBy(0)
                //指定窗口，每10秒个计算一次
                .timeWindow(Time.of(10, TimeUnit.SECONDS))
                //计算个数，计算第1列
                .sum(1);
        //4.打印输出sink
        result.print();
        //5.开始执行
        env.execute();
    }
    public static class LineSplitter implements FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String line, Collector<Tuple2<String, Integer>> collector) throws Exception {
            //根据空格进行切割
            for (String word : line.split(" ")) {
                collector.collect(new Tuple2<>(word, 1));
            }
        }
    }
}