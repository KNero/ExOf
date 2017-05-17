function sendRequest(_type, _data, _success) {
	var req = {};
	req.type = _type;
	req.parameter = _data;
	
	$.ajax({
		method : "POST",
		url : "/api",
		data : JSON.stringify(req),
		async : false,
		success : function(_res) {
			console.log("ajax response : "  + _res);
			
			var resData;
			if (_res) {
				try {
					resData = JSON.parse(_res);
				} catch (e) {
					resData = _res;
				}
			}
			
			if (resData.result == "No data.") {
				location.replace("index.html");
				return
			}
			
			_success(resData);
		},
		error : function() {
			alert("Fail!");
		}
	});
}

function isResultSuccess(_res) {
	return _res.result == "Success";
}

