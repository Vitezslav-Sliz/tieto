var selected;
var versionInJson;

function render() {
	var currentVersion = versionInJson[selected];
	var nodeRef = $("#versionSelect option:selected").text(); 
	$.ajax({
		url: serviceContext + "/tieto/healthy-addon/folder-hierarchy/list-nodes-for-job.json",
		method: "GET",
		data: {"nodeRef" : nodeRef}
	}).done(function( data ) {
		$("#time").text(data.time);
		$("#server").text(data.server);
		renderCount(data.data.count);
		renderDepth(data.data.depth);
		$(".path").shorten();
	}).fail(function(jqXHR, textStatus) {
		console.log("Error while loading list of nodes for job");
	});
	
}

$(function() {
	selected = 0;
	$.ajax({
		url: serviceContext + "/tieto/healthy-addon/folder-hierarchy/list-jobs.json",
	}).done(function( data ) {
		versionInJson = data; 
		renderSelect(versionInJson);
		render();
	}).fail(function(jqXHR, textStatus) {
		console.log("Error while loading list of jobs");
	});
	
	$("#versionSelect").change(function() {
		selected = $("#versionSelect").val();
		$("#versionSelect option[value="+ selected + "]").prop('selected',true);
		render();
	})

});

function renderCount(data) {
	var htmlCount = "<div class=\"node-info\"><table>";
	for (var i=0; i < data.length; i++) {
		htmlCount += "<tr><td class=\"node-header\">NodeRef:</td><td>" + data[i].nodeRef + "</td></tr>";
		htmlCount += "<tr><td class=\"node-header\">Path:</td><td><span class=\"path\">" + data[i].path + "</td></tr>";
		htmlCount += "<tr><td class=\"node-header\">Node count:</td><td>" + data[i].nodeCount + "</td></tr>";
	}
	htmlCount += "</table></div>"; 
	$("#count").html(htmlCount);
}

function renderDepth(data) {
	var htmlDepth = "<div class=\"node-info\"><table>";
	for (var i=0; i < data.length; i++) {
		htmlDepth += "<tr><td class=\"node-header\">NodeRef:</td><td>" + data[i].nodeRef + "</td></tr>";
		htmlDepth += "<tr><td class=\"node-header\">Path:</td><td><span class=\"path\">" + data[i].path + "</td></tr>";
		htmlDepth += "<tr><td class=\"node-header\">Node count:</td><td>" + data[i].nodeCount + "</td></tr>";
	}
	htmlDepth += "</table></div>"; 
	$("#depth").html(htmlDepth);
}

function renderSelect(data) {
	var htmlSelect = "";
	for (var i=0; i < data.length; i++) {
		htmlSelect += "<option value=\"" + i + "\">" + data[i].nodeRef + "</option>"
	}
	$("#versionSelect").html(htmlSelect);
}
