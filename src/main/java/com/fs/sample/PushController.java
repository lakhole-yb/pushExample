package com.fs.sample;

import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PushController {

	@RequestMapping(value = "/sw.js", method = RequestMethod.GET)
	@ResponseBody
	public String swPushNotification(Locale locale, Model model,HttpServletResponse res) {
		
		res.setHeader("Content-Type","text/javascript");
		
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

}
