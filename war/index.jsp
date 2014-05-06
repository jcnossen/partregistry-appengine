<%@page import="java.util.List"%>
<%@page import="javax.jdo.Query"%>
<%@page import="javax.jdo.PersistenceManager"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" 
	import="partable.webdb.server.*"%>
<jsp:include page="header.jsp"></jsp:include>

<div class="rbox">
<h1>
	Parts query service site for <a href="http://partsregistry.org">partsregistry.org</a>
</h1>

<p>
WORK IN PROGRESS
<br>
You can find parts here based on their properties and categories
<br>
Test it in the box below, or use the <a href="/api.jsp">web API's</a>
</p>

</div>

<div class="rbox">

<style>
.catitem {
	cursor: pointer;
	margin: 3px;
}
</style>

<div id="catlist">

<%
PersistenceManager pm = PMF.get();
Query q = pm.newQuery(Category.class);

@SuppressWarnings("unchecked")
List<Category> cats = (List<Category>) q.execute();

int i = 0;
for (Category c : cats) {
	%>
	<div class="catitem" id="cat-<%= i %>"><%= c.name %></div>
	<div class="catresult" id="cat-<%=i%>-results"></div>
	<%
	i++;
}

q.closeAll();
PMF.release();

%>

</div>

<script type="text/javascript">

String.prototype.startsWith = function(str){
    return (this.indexOf(str) === 0);
}

function listParts(cat, dst) {
	var lp = function(data) {
		var html = "<div id='partlist'>";
		for(var i = 0; i<data.length;i++) {
	 		var p = data[i];
	 		html += "<div>";
	 		html += '<a href="http://partsregistry.org/wiki/index.php?title=Part:' + p.name + '">';
	 		html += p.name + "</a>: " + p.desc;
	 		html += "</div>\n";
		}
	 	html += '</div>';
	 
	 	$(".catresult").slideUp();
		dst.html(html);
		dst.slideDown();
	};
	
	$.ajax( {
		url: "/query/json?param=categories&value="+cat,
		success: function(data) { 
			var parts = $.parseJSON(data);
			lp(parts); 
		},
		error: function(xmlobj, status) {
			$("#result").text("Error loading data");
		},
		dataType: 'text'
	});
}

$(function() {
	
	$(".catresult").hide();
	
	$(".catitem").click(function() {
		var results = $("#" + this.id + "-results");
		if (results.is(":visible"))
			results.slideUp();
		else
			listParts( $(this).text(), results );
	});
});

</script>

</div>

<jsp:include page="footer.jsp"></jsp:include>