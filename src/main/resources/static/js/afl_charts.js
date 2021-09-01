const charts_colors = ['red','green','blue','orange','yellow','pink'];
const chart_types = {
	line: {
		datase_design_fn:function(dataset, i){
				dataset.borderColor = charts_colors[i];
		},
		template:{
		  type: "line",
		  options: {
			responsive: true,
			aspectRatio: 4,
			interaction: {
			  mode: 'index',
			  intersect: false,
			},
			stacked: false,
			tooltips: {
			  mode: 'label',
			},
			hover: {
			  mode: 'nearest',
			  intersect: true
			},
			plugins:{
				legend:{}
			}
		  }
		}
	},
	bar: {
		datase_design_fn:function(dataset, i){
				dataset.borderColor = 'BLACK';
				dataset.backgroundColor = charts_colors[i];
		},
		template:{
		  type: "bar",
		  options: {
			responsive: true,
			interaction: {
			  mode: 'index',
			  intersect: false,
			},
			stacked: false,
			tooltips: {
			  mode: 'label',
			},
			hover: {
			  mode: 'nearest',
			  intersect: true
			},
			plugins: {
				title: {
				display: true,
				text: ""
			  }
			}
		  }
		}
	},
	doughnut:{
		datase_design_fn:function(dataset, i){
				dataset.borderColor = 'BLACK';
				dataset.backgroundColor = charts_colors[i];
				dataset.borderWidth = 2;
				dataset.offset = 25;
				dataset.spacing = 5
				
		},
		template:{
			type: "doughnut",		  
			options: {
				maintainAspectRatio: false,
				circumference: 180,
				radius: 100,
				rotation: -90,
				cutout: '50%',
				responsive: true,
				animation: {
					animateScale: false,
					animateRotate: true
				},
				plugins: { title: { display: true,text: "" }
				}
			}
		}
	}
};


class AflChart{		
	empty_data = {
	  labels: [''],
	  datasets: [
		{
			label: '',
			data: [],
			borderColor: "rgb(255,165,0)",
			backgroundColor: "rgb(255,165,0,0.1)",
			fill: false,
			lineTension: 0.3,
			pointBorderWidth: 0.1,
		    pointHoverRadius: 10,
		    pointHoverBackgroundColor: "yellow",
		    pointHoverBorderColor: "brown",
		    pointHoverBorderWidth: 1,
		    pointRadius: 0.5
		}
	   ]
	};
	
	constructor(chart_fields){
		this.getRequestParameters = chart_fields.params_fn,
		this.url = chart_fields.url,
		this.chart_type = chart_fields.chart_type,
		this.name = chart_fields.name,
		this.step_ms = chart_fields.step_ms,
		this.chart_id = chart_fields.chart_id,
		this.charts_container_id = chart_fields.charts_container_id,
		this.html_element = `<canvas id='${chart_fields.chart_id}'></canvas>`;
		
	};
	
	createNewChart(){
		this.#create_html_block();
		this.chart = this.#addNewChart(this.empty_data, this.name, this.chart_id);
	};
	
	
	#addNewChart(data, title, elementId){
		var ctx = document.getElementById(elementId).getContext('2d');
		this.chart_context = ctx;
		let chartTemplateConfig = Object.assign({},chart_types[this.chart_type].template);
		chartTemplateConfig.data = data;
		console.log(chartTemplateConfig);
//		chartTemplateConfig.options.plugins.legend = { title:{ display: true, text: title} };
		var newChart = new Chart(ctx, chartTemplateConfig);	
		return newChart;
	}
	
	#create_html_block(){
		$('#' + this.charts_container_id).append(this.html_element);
	}
	reportData(params){
		var json_response;
		$.ajax({
			type: 'post',
			dataType: 'json',
			contentType: 'application/json',
			async: false,
			url: this.url,
			data: JSON.stringify(params),
			success: function(response){
				json_response = response;
			}
		});
		return json_response;
	}

	#returnReportData(params){
		var json_response;
		$.ajax({
	            type: 'post',
	            dataType: 'json',
		    contentType: 'application/json',
		    async: false,
		    url: this.url,
		    data: JSON.stringify(params),
		    success: function(response){
				json_response = response;
		    }
		});
		return json_response;
	}
	
	#returnChartsData(json_response){
		if("doughnut" == this.chart_type){
			return this.#returnChartsData_doughnut(json_response);
		}else{
			return this.#returnChartsData_default(json_response);
		}
	
	}
	
	#returnChartsData_doughnut(json_response){
		var self = this;
		var dataset = json_response.values.map(function(value) {
	            var item = {};
				
				item.backgroundColor = ["rgba(255, 99, 132)","rgba(30, 130, 76, 1)","rgba(54, 162, 232)","rgba(255, 205, 86)"],
				//chart_types[self.chart_type].datase_design_fn(item, i);
				//item.label = metric.legend;
				item.label = "# of Votes";
				item.data = json_response.types.map(function(type, i) {
				    return value[type.fieldName];;  
				});
			return item;
		});
		var labels = json_response.types.map(function(metric, i) {
                return metric.legend;
		});
		return {
			labels: labels,
			datasets: dataset
		}
	}
	
	#returnChartsData_default(json_response){
		var self = this;
		var dataset = json_response.types.map(function(metric, i) {
	            var item = {};
				chart_types[self.chart_type].datase_design_fn(item, i);
				item.label = metric.legend;
				item.data = json_response.values.map(function(value) {
				   return value[metric.fieldName];  
				});
			return item;
		});
		var labels = json_response.values.map(function(value) {
                //return value.dateTime;
				return new Date(value.dateTime).toLocaleTimeString()
		});
		return {
			labels: labels,
			datasets: dataset
		}
	}

	#refreshChart(params){
		var json_response = this.#returnReportData(params);
		this.chart.data = this.#returnChartsData(json_response);
		this.chart.update();
		
	}

	startChartRefreshing(){
		var self = this;
		this.#refreshChart(this.getRequestParameters());
		var intervalId = window.setInterval(function(){
			self.#refreshChart(self.getRequestParameters());
		}, self.step_ms);
	}
}




