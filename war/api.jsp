<%@ page language="java" contentType="text/html; charset=Cp1252" 
	import="partable.webdb.server.*"
    pageEncoding="Cp1252"%>
<jsp:include page="header.jsp"></jsp:include>

<style>
.apimethod {
	margin: 10px;
	background-color: #eee;
	padding: 4px;
	-moz-border-radius: 15px;
	border-radius: 15px;
}
.apiurl {
	font-family: courier;
}
.apiexample {
	margin: 10px;
}

</style>

<div class="rbox">
	<h1>Web API</h1>

	<h2>Methods</h2>	
	<div class="apimethod">
		<div class="apidesc">Get part JSON for all parts that have a property with given value.</div>
		<div class="apiurl">/query/json?param=PROPERTY&value=VALUE&jsonp=JSFUNCTION</div>
		<div class="apiexample">
			Examples:
			Parts coding for membrane proteins: 
			<a href="/query/json?param=categories&value=/cds/membrane">/query/json?param=categories&amp;value=/cds/membrane</a>

			<br>
			Parts that are using a 0.3 Ribosome binding site part: 
			<a href="/query/json?param=subparts&value=BBa_B0032">/query/json?param=subparts&amp;value=BBa_B0032</a>
		</div>
	</div>
	<div class="apimethod">
		<div class="apidesc">Get all part names that have a property with given value</div>
		<div class="apiurl">/query/simple?type=parts&amp;param=PROPERTY&amp;value=VALUE</div>
		<div class="apiexample">
			Example:
			<br>
			Parts that code for a protein 
			<a href="/query/simple?param=type&value=Coding">/query/simple?param=type&amp;value=Coding</a>
		</div>
	</div>
	<div class="apimethod">
		<div class="apidesc">List all categories</div>
		<div class="apiurl">/query/simple?type=cat</div>
		<div class="apiexample">
			Example:
			<br>
			<a href="/query/simple?type=cat">/query/simple?type=cat</a>
		</div>
	</div>
	<div class="apimethod">
		<div class="apidesc">Get original XML for a single part</div>
		<div class="apiurl">/query/simple?type=partxml&amp;part=PART_NAME</div>
		<div class="apiexample"><a href="/query/simple?type=partxml&part=BBa_B0032">/query/simple?type=partxml&amp;part=BBa_B0032</a></div>
	</div>
	
	<h2>Part Properties</h2>
	<ul>
		<li>name</li>
		<li>description</li>
		<li>author</li>
		<li>type</li>
		<li>group (Work in progress)</li>
		<li>categories</li>
		<li>subparts</li>
		<li>allSubparts: full list of subparts (including subparts of subparts etc..)</li>
		<li>results</li>
	</ul>
	
</div>

<jsp:include page="footer.jsp"></jsp:include>