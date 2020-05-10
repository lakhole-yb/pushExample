package com.fs.sample;

import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

@Controller
public class PushController {

	@RequestMapping(value = "/sw.js", method = RequestMethod.GET,produces="application/javascript")
	@ResponseBody
	public String swPushNotification(Locale locale, Model model,HttpServletResponse res) {
		
		
		String snippet=""
				+ "self.addEventListener('push', event => {\n" + 
				"    let body;\n" + 
				"  \n" + 
				"    if (event.data) {\n" + 
				"      body = event.data.text();\n" + 
				"    } else {\n" + 
				"      body = 'Default body';\n" + 
				"    }\n" + 
				"  \n" + 
				"    const options = {\n" + 
				"      body: body,\n" + 
				"      icon: 'images/notification-flat.png',\n" + 
				"      vibrate: [100, 50, 100],\n" + 
				"      data: {\n" + 
				"        dateOfArrival: Date.now(),\n" + 
				"        primaryKey: 1\n" + 
				"      },\n" + 
				"      actions: [\n" + 
				"        {action: 'https://touchbase.live', title: 'Touchbase Live',\n" + 
				"          icon: 'images/checkmark.png'},\n" + 
				"        {action: 'close', title: 'Close the notification',\n" + 
				"          icon: 'images/xmark.png'},\n" + 
				"      ]\n" + 
				"    };\n" + 
				
				"  \n" + 
				"    event.waitUntil(\n" + 
				"      self.registration.showNotification('TouchBase Live', options)\n" + 
				"    );\n" + 
				"  });";
		
		return snippet;
	}
	
	@RequestMapping(value="/subscribe",method=RequestMethod.POST)
	public ResponseEntity<String> subscribe(@RequestBody String body) {
		
		System.out.println("Request Payload: "+body);
		Gson g = new GsonBuilder().setLenient().serializeNulls().create();
		JsonObject payload=g.fromJson(body,JsonObject.class);
		
		System.out.println("Payload: "+payload);
		
		try{
			PushMessage pm=new PushMessage();
			pm.sendNotification(payload);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
		return (ResponseEntity<String>) ResponseEntity.ok();
	}
	
	public static void main(String[] args) {
		String json = "{\"field\":\"abc\"}";
		Gson g = new GsonBuilder().setLenient().serializeNulls().create();
		JsonObject j=g.fromJson(json,JsonObject.class);
		
		System.out.println(j.get("field").getAsString());
		
	}

}
