function getAllList(_settingName) {
	sendRequest("getDynamicSettingList", {"name" : _settingName}, function(_res) {
		var table = $("#settingTable").get(0);
		
		for (var i in _res) {
			var name = _res[i].name;
			var value = _res[i].value;
			var des = _res[i].description;
			
			var tr = document.createElement("tr");
			tr.innerHTML += "<td>" + name + "</td>";
			tr.innerHTML += "<td>" + value + "</td>";
			tr.innerHTML += "<td>" + des + "</td>";
			table.appendChild(tr);
		}
	});
}