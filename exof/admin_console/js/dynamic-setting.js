function getAllList(_settingName) {
	sendRequest("getDynamicSettingList", {"name" : _settingName}, function(_res) {
		$("#settingTable").empty();
		var table = $("#settingTable").get(0);
		
		for (var i in _res) {
			var name = _res[i].name;
			var value = _res[i].value;
			var des = _res[i].description;
			
			var tr = document.createElement("tr");
			tr.style.cursor = "pointer";
			tr.onclick = showUpdateModal(name, value, des);
			tr.innerHTML = "<td>" + name + "</td><td>" + value + "</td><td>" + des + "</td></tr>";
			table.appendChild(tr);
		}
	});
}

function showAddModal() {
	$("#modal_update").hide();
	$("#modal_delete").hide();
	$("#modal_add").show();
	
	$(".ui .message").attr("ui hidden message");
	$("#modal_name").val("");
	$("#modal_value").val("");
	$("#modal_des").val("");
	$("#update_setting").modal("show");
}

function showUpdateModal(_name, _value, _des) {
	return function() {
		$("#modal_update").show();
		$("#modal_delete").show();
		$("#modal_add").hide();
		
		$("#modal_name").val(_name);
		$("#modal_value").val(_value);
		$("#modal_des").val(_des);
		
		$("#update_setting").modal("show");
	};
}

var dynamicSetting = (function() {
	$("#modal_add").click(function() {
		dynamicSetting.add($("#modal_name").val(), $("#modal_value").val(), $("#modal_des").val());
	});
	
	$("#modal_update").click(function() {
		dynamicSetting.update($("#modal_name").val(), $("#modal_value").val(), $("#modal_des").val());
	});
	
	$("#modal_delete").click(function() {
		dynamicSetting.remove($("#modal_name").val());
	});
	
	return {
		update : function(_name, _value, _des) {
			$(".ui .message").attr("ui hidden message");
			if (_name.length == 0 || _value.length == 0) {
				return;
			}
			
			$("#confirm_hader").text("Are you sure you want to Update?");
			$("#confirm").modal({
				onApprove : function() {
					var param = {};
					param.name = _name;
					param.value = _value;
					param.des = _des;
					sendRequest("updateDynamicSetting", param, function(_res) {
						if(isResultSuccess(_res)) {
							getAllList("");
							$("#update_setting").modal("hide");
						} else {
							$("#modal_alert").text(_res.result);
							$(".ui .message").toggleClass("hidden visible");
						}
					});
				},
				onDeny : showUpdateModal(_name, _value, _des)
			}).modal("show");
		},
		
		remove : function(_name) {
			$(".ui .message").attr("ui hidden message");
			if (_name.length == 0) {
				return;
			}
			
			$("#confirm_hader").text("Are you sure you want to delete?");
			$("#confirm").modal({
				onApprove : function() {
					var param = {};
					param.name = _name;
					sendRequest("removeDynamicSetting", param, function(_res) {
						if (isResultSuccess(_res)) {
							getAllList("");
							$("#update_setting").modal("hide");
						} else {
							$("#modal_alert").text(_res.result);
							$(".ui .message").toggleClass("hidden visible");
						}
					});
				}
			}).modal("show");
		},
		
		add : function(_name, _value, _des) {
			$(".ui .message").attr("ui hidden message");
			if (_name.length == 0 || _value.length == 0) {
				return;
			}
			
			var param = {};
			param.name = _name;
			param.value = _value;
			param.des = _des;
			sendRequest("addDynamicSetting", param, function(_res) {
				if (isResultSuccess(_res)) {
					getAllList("");
					$("#update_setting").modal("hide");
				} else {
					$("#modal_alert").text(_res.result);
					$(".ui .message").toggleClass("hidden visible");
				}
			});
		}
	};
})();
