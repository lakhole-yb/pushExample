package com.fs.sample;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		return "home";
	}
	
	@RequestMapping(value = "/sw.js", method = RequestMethod.GET)
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
