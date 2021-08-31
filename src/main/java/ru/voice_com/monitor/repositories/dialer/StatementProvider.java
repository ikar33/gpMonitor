package ru.voice_com.monitor.repositories.dialer;

public class StatementProvider {

    public static String unprocessedDialsWaitTime() {
        //language=PostgreSQL
        return "select\n" +
                "max_wait_time,\n" +
                "case when max_wait_time < #{limit_seconds} then #{limit_seconds} - max_wait_time else 0 end as remains\n" +
                "from (select \n" +
                "\t\t\t\tcoalesce(ROUND(max(EXTRACT(EPOCH FROM (coalesce(startdialtime, now())::timestamp - callafterdate::timestamp)))),0) as max_wait_time\n" +
                "\tfrom \n" +
                "\tdialjob_record \n" +
                "\twhere createdate between (now()  - CAST(#{interval_seconds} AS INTERVAL)) and now()) s\n";
    }

    public static String dialsAggregateStatistic() {
        //language=PostgreSQL
        return "select\n" +
                "end_interval as \"dateTime\",\n" +
                "coalesce(max(EXTRACT(EPOCH FROM (coalesce(startdialtime, now())::timestamp - callafterdate::timestamp)))\n" +
                "\t\tfilter (where date_trunc('minute',callafterdate) between start_interval and end_interval), 0) as max_wait_time,\n" +
                "coalesce(avg(EXTRACT(EPOCH FROM (enddialtime - connectdialtime)))\n" +
                "\t\tfilter (where date_trunc('minute',connectdialtime) between start_interval and end_interval), 0) as avg_dial_time\n" +
                "from\n" +
                "(select \n" +
                " \tdistinct #{reportDate}  + interval '1 minutes'*offs  as start_interval,\n" +
                " \t#{reportDate}  + interval '1 minutes'*(offs + #{intervalInMinutes})  as end_interval\n" +
                "from generate_series(0,1440,#{intervalInMinutes}) as offs order by start_interval) per\n" +
                "left join dialjob_record dr on dr.createdate > #{reportDate}\n" +
                "where end_interval < now() \n" +
                "group by start_interval,end_interval";
    }

    //language=PostgreSQL
    public static String callsStatisticQuery() {
        return "select\n" +
                "date_trunc('minute',end_interval) as \"dateTime\",\n" +
                "count(*) filter (where callafterdate < end_interval and (startdialtime is null or startdialtime > end_interval)) as calls_in_order,\n" +
                "count(*) filter (where date_trunc('minute',startdialtime) between start_interval and end_interval) as calls_in_process,\n" +
                "count(*) filter (where date_trunc('minute',startdialtime) between start_interval and end_interval and cause = 4) as successfull_calls,\n" +
                "count(*) filter (where date_trunc('minute',startdialtime) between start_interval and end_interval and cause <> 4) as aborted_calls\n" +
                "from\n" +
                "(select \n" +
                " \tdistinct #{reportDate} + interval '1 minutes'*offs  as start_interval,\n" +
                " \t#{reportDate} + interval '1 minutes'*(offs + #{intervalInMinutes})  as end_interval\n" +
                "from generate_series(0,1440,#{intervalInMinutes}) as offs order by start_interval) per\n" +
                "left join dialjob_record dr on dr.createdate > #{reportDate}\n" +
                "where end_interval < now() \n" +
                "group by start_interval,end_interval\n";
    }
}
