<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Home</title>
</head>
<body>
<body>
    <h2  id="datetime"></h2>
    <h3 class="datetime">Subscribe for Notification, So you will never miss an important updates!</h3>
    <button onClick="subscribe()">Subscribe</button>

    <script type="text/javascript">
        let interval=setInterval(function(){
            document.getElementById("datetime").innerHTML=" Current Time is : "+new Date().toLocaleTimeString()
        },1000);

        /* addEventListener('load',async() => {
            let sw = await navigator.serviceWorker.register('resources/sw.js',{scope:'resources/'});
            console.log(sw);
        }); */
        if ('serviceWorker' in navigator) {
        	  window.addEventListener('load', function() {
        	    navigator.serviceWorker.register('https://evening-meadow-19282.herokuapp.com/resources/sw.js',{scope:'/resources/'})
        	    .then(function(registration) {
        	   		// Registration was successful
        	      	console.log('ServiceWorker registration successful with scope: ', registration.scope);
        	    	console.log(registration);
        	    	
        	    	navigator.serviceWorker.ready
                	.then(function(registration){
                		console.log('Registration state: '+registration.active);
                		let push = registration.pushManager.subscribe({
                            userVisibleOnly:true,
                            applicationServerKey:'BLid-Ld7O47We3JNKcqnIkgJjQ8sgLpf8BX7FBqtn_dE5tfNWY4h-QzbCdpnPHbVimi2t1Jf3Qvysq-6e-hiotQ'
                        });
                		console.log(JSON.stringify(push,null,2));
                	},function(err){
                		console.log('Error in sw ready'+err);
                	});
        	    	
        	    }, function(err) {
        	    	// registration failed :(
        	      	console.log('ServiceWorker registration failed: ', err);
        	    });
        	  });
        }else{
        	console.log('No Service Worker is Supported')
        }

        function subscribe(){
            
        }

    </script>
</body>
</html>
