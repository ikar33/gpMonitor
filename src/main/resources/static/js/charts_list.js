let charts_config = {     					/*Массив графиков/отчетов*/
	created_charts:[],
	charts:[
	{

		url: location.pathname + "dialstat/get-calls-stat", /*URL отчета*/
		params_fn: function(){                              /*Функция для инициализации параметров запроса к сервису отчетов*/  
			var start_dt = new Date();
			start_dt.setUTCHours(0,0,0,0);
			return{
				report_date: start_dt.toISOString(),
			}
		}, 
		start_date: '',
		name: "Общая статистика исходящих вызовов", /*Отображаемый TITLE*/
		step_ms: 300000,                            /*Частота обвноления отчета*/
		chart_id: "callsStatChart",                /*Уникальный ID обекта - графика (chart.js)*/
		charts_container_id: 'calls_stats_block',  /*HTML блок куда будет встраиваться грфик*/
		chart_type: "line"		            /*Тип графика (chart.js)*/
	},
	{
		url: location.pathname + "dialstat/get-dial-agg-stat",
		params_fn: function(){
				var start_dt = new Date();
				start_dt.setUTCHours(0,0,0,0);
				return{
					report_date: start_dt.toISOString(),
				}
			}, 
		start_date: '',
		name: "Статистика исходящих вызовов",
		step_ms: 60000,
		chart_id: "aggCallsStatChart",
		charts_container_id: 'calls_agg_st_block',
		chart_type: "line"
	},
	{
		url: location.pathname + "dialstat/get-calls-plan",
		params_fn: function(){
				return {};
			}, 
		start_date: '',
		name: "Статистика исходящих вызовов",
		step_ms: 60000,
		chart_id: "callsPlan",
		charts_container_id: 'calls_plan',
		chart_type: "line"
	},
	{
		url: location.pathname + "dialstat/get-processing-wait-time",
		params_fn: function(){
				return {
					limitInSec: 120,
					periodInSec: 600
				};
		}, 
		start_date: '',
		name: "Время ожидания обработки звонка ",
		step_ms: 5000,
		chart_id: "callsWaitTime",
//		charts_container_id: 'calls_wait_time',
		charts_container_id: 'calls_eait_time_block',
		chart_type: "doughnut"
	}
	]
};



function createAndStartCharts(){
		charts_config.created_charts = charts_config.charts.map(function(el){
			new_chart_obj = new AflChart(el);
			new_chart_obj.createNewChart();
			new_chart_obj.startChartRefreshing();
			return new_chart_obj;
		})
}
