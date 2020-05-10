package com.fs.sample;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
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
		try {
			body=URLDecoder.decode(body,"UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
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
	
	@RequestMapping(value="/getPubKey",method=RequestMethod.GET)
	public ResponseEntity<String> getPubKey(){
		
		PushMessage p = null;
		try {
			p = new PushMessage();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ResponseEntity.ok(p.getPublickKey());
		
	}
	
	public static void main(String[] args) {
		String json = "{\"endpoint\":\"https://fcm.googleapis.com/fcm/send/ff-haWrvMbc:APA91bFysJjJ9xsNnea6LNnuVDIsCyqbdjg3qWzKxKVmzL04vnLB1_EkV843JRLB3PZYwEloWo24fCkQA7bK0V_RtEP2iTdvDe75jxL6rkmiw5CyOisxL6tc1-0ab9K45wCCBdjU7uym\",\"expirationTime\":null,\"keys\":{\"p256" + 
				"dh\":\"BDFXpEAro1r7_BWjqS28sDZ9YIfqxlGP1HggqL0jUEYc8uN8Ps6L7Jhku0MsIQ32sM9cLDuopkwZ5sf7eNNEb4I\",\"auth\":\"AsiOmA0LjCps5hF6hm35Sw\"}}";
		System.out.println(json);
		Gson g = new GsonBuilder().setLenient().serializeNulls().create();
		JsonObject j=g.fromJson(json,JsonObject.class);
		
		System.out.println(j.get("endpoint").getAsString());
		ClassLoader loader = ClassLoader.getSystemClassLoader();
		try {
			System.out.println(loader.getResource("private.key"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
