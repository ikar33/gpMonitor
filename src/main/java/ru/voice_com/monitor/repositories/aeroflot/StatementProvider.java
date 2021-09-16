package ru.voice_com.monitor.repositories.aeroflot;

public class StatementProvider {

    public static String callsDailyPlan(){
        //language=PostgreSQL
        return "\t\n" +
                "SELECT \n" +
                "count(*) as all_attemps,\n" +
                "count(*) filter (where priority = 1) as higth_priority,\n" +
                "count(*) filter (where priority <> 1) as middle_priority,\n" +
                "date_time_t as \"dateTime\"\n" +
                "\tFROM(\n" +
                "\tSELECT DISTINCT ON (t.id, ph.phone)\n" +
                "\t\t\t    t.id AS task, ph.phone, t.remark, cd.id AS contact_date, t.priority,\n" +
                "\t\t\t\t\tcd.datetime::time + coalesce(ph.gmt, 3) * INTERVAL '1 hour' - INTERVAL '3 hours',\n" +
                "\t\t\t\t\tcd.datetime,\n" +
                "\t\t \t\t\tph.gmt,\n" +
                "\t\t\t\t\tcd.datetime + coalesce(ph.gmt, 3) * INTERVAL '1 hour' - INTERVAL '3 hours' as dt,\n" +
                "\t\t \t\t\tcase\n" +
                "\t\t\t\t\twhen  t.priority = 1 then date_trunc('minute', cd.datetime)\n" +
                "\t\t\t\t\twhen  t.priority <> 1 \n" +
                "\t\t\t\t\t\tand (cd.datetime::time + coalesce(ph.gmt, 3) * INTERVAL '1 hour' - INTERVAL '3 hours') between '09:00' and '21:00'\n" +
                "\t\t\t\t\t\tthen cd.datetime\n" +
                "\t\t\t\t\twhen  t.priority <> 1 \n" +
                "\t\t\t\t\t\tand (cd.datetime + coalesce(ph.gmt, 3) * INTERVAL '1 hour' - INTERVAL '3 hours') < now()::date + INTERVAL '9 hour'\n" +
                "\t\t\t\t\t\t--then cd.datetime + '09:00' - (cd.datetime::time + coalesce(ph.gmt, 3) * INTERVAL '1 hour' - INTERVAL '3 hours')\n" +
                "\t\t \t\t\t\tthen now()::date + INTERVAL '9 hour' -  coalesce(ph.gmt, 3) * INTERVAL '1 hour' + INTERVAL '3 hours'\n" +
                "\t\t\t\t\telse '2021-10-10' end as date_time_t\n" +
                "\t\t    FROM\n" +
                "\t\t\t    tasks t\n" +
                "\t\t\t    JOIN pnr p ON (t.id = p.task)\n" +
                "\t\t\t    JOIN phones ph ON (p.id = ph.pnr)\n" +
                "\t\t\t    JOIN contact_dates cd ON (cd.task = t.id)\n" +
                "\t\t\t    LEFT JOIN calls_out co ON (co.contact_date = cd.id)\n" +
                "\t\t    WHERE\n" +
                "\t\t\t    t.status = 4\n" +
                "\t\t\t    AND p.pass_status NOT IN (5,6,7)\n" +
                "\t\t\t    AND p.list_status NOT IN (3,4)\n" +
                "\t\t\t    AND (NOT ph.is_completed OR ph.is_completed IS NULL)\n" +
                "\t\t\t    AND NOT p.is_test\n" +
                "   \t\t\t    AND cd.datetime::date between now()::date - INTERVAL '1 day' and now()::date \n" +
                "\t\t\t\t--AND cd.datetime::date between '2021-09-02 00:00:00' and '2021-09-03 00:00:00'\n" +
                "\t\t\t    AND cd.id NOT IN (\n" +
                "\t\t\t\t    SELECT co.contact_date\n" +
                "    \t\t\t\tFROM\n" +
                "    \t\t\t\t   calls_out co\n" +
                "    \t\t\t\t   JOIN contact_dates cd2 ON (cd2.id = co.contact_date)\n" +
                "    \t\t\t\tWHERE\n" +
                "    \t\t\t\t   ph.phone = co.phone\n" +
                "    \t\t\t\t   AND cd2.task = t.id\n" +
                "\t\t\t    )\n" +
                "     \t) main group by priority, date_time_t\n" +
                "                order by date_time_t\n" +
                "\t\t";
    }

    //language=PostgreSQL
    private static final String createdTasks =
            "SELECT create_date::date                       AS date,\n" +
                    "       extract(HOUR FROM create_date)::integer AS hour,\n" +
                    "       count(*)                                AS count\n" +
                    "FROM tasks\n" +
                    "WHERE create_date::date BETWEEN #{from} AND #{till}\n" +
                    "  AND priority = #{priority}\n" +
                    "GROUP BY date, hour\n" +
                    "ORDER BY date, hour ASC";

    //language=PostgreSQL
    private static final String createdTlssTasks =
            "SELECT startdatetime::date                       AS date,\n" +
                    "       extract(HOUR FROM startdatetime)::integer AS hour,\n" +
                    "       count(*)                                  AS count\n" +
                    "FROM tl_ss_tasks\n" +
                    "WHERE startdatetime::date BETWEEN #{from} AND #{till}\n" +
                    "GROUP BY date, hour\n" +
                    "ORDER BY date, hour ASC";


    //language=PostgreSQL
    private static final String ivrCalls =
            "SELECT startdatetime::date                       AS date,\n" +
                    "       extract(HOUR FROM startdatetime)::integer AS hour,\n" +
                    "       count(*)                                  AS count\n" +
                    "FROM call\n" +
                    "WHERE app = 3\n" +
                    "  AND startdatetime::date BETWEEN #{from} AND #{till}\n" +
                    "GROUP BY date, hour\n" +
                    "ORDER BY date, hour ASC";

    //language=PostgreSQL
    private static final String completedCommonRemarks =
            "SELECT lasttry::date                       AS date,\n" +
                    "       extract(HOUR FROM lasttry)::integer AS hour,\n" +
                    "       count(*)                            AS count\n" +
                    "FROM remarks\n" +
                    "WHERE lasttry::date BETWEEN #{from} AND #{till}\n" +
                    "GROUP BY date, hour\n" +
                    "ORDER BY date, hour ASC";

    //language=PostgreSQL
    private static final String completedTlssRemarks =
            "SELECT lasttry::date                       AS date,\n" +
                    "       extract(HOUR FROM lasttry)::integer AS hour,\n" +
                    "       count(*)                            AS count\n" +
                    "FROM tl_ss_remarks\n" +
                    "WHERE lasttry::date BETWEEN #{from} AND #{till}\n" +
                    "GROUP BY date, hour\n" +
                    "ORDER BY date, hour ASC";

    //language=PostgreSQL
    private static final String allCompletedRemarks =
            "SELECT date, hour, count(*)\n" +
                    "FROM (SELECT lasttry::date                       AS date,\n" +
                    "             extract(HOUR FROM lasttry)::integer AS hour\n" +
                    "      FROM remarks\n" +
                    "      WHERE lasttry::date BETWEEN #{from} AND #{till}\n" +
                    "      UNION ALL\n" +
                    "      SELECT lasttry::date                       AS date,\n" +
                    "             extract(HOUR FROM lasttry)::integer AS hour\n" +
                    "      FROM tl_ss_remarks\n" +
                    "      WHERE lasttry::date BETWEEN #{from} AND #{till}\n" +
                    "     ) a\n" +
                    "GROUP BY date, hour\n" +
                    "ORDER BY date, hour ASC";

    public static String getCreatedTasks() {
        return createdTasks;
    }

    public static String getCreatedTlssTasks() {
        return createdTlssTasks;
    }

    public static String getIvrCalls() {
        return ivrCalls;
    }

    public static String getCompletedCommonRemarks() {
        return completedCommonRemarks;
    }

    public static String getCompletedTlssRemarks() {
        return completedTlssRemarks;
    }

    public static String getAllCompletedRemarks() {
        return allCompletedRemarks;
    }
}
