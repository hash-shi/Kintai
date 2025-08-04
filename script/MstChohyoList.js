function toggleVisibility(){
	proc("change",{}, function(data){
		
		if (data == undefined){ return; }
		if (data["contents"] == undefined){ return; }

		var contents				= data["contents"];
		if (contents["result"] == undefined){ return; }
		var result			= contents["result"];
		console.log(result);
		
		const div = document.getElementById('outputArea');
		div.style.display = div.style.display === 'none' ? 'block' : 'none';
		
	});
}