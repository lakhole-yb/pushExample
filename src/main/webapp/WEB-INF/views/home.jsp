<html>
<head>
	<title>Home</title>
</head>
<body>
<body>
    <h2  id="datetime"></h2>
    <h3 class="datetime">Subscribe for Notification, So you will never miss an important updates!</h3>
    <button onClick="subscribe()">Subscribe</button>
	<script
  src="https://code.jquery.com/jquery-3.5.1.min.js"
  integrity="sha256-9/aliU8dGd2tb6OSsuzixeV4y/faTqgFtohetphbbj0="
  crossorigin="anonymous"></script>
    <script type="text/javascript">
        let interval=setInterval(function(){
            document.getElementById("datetime").innerHTML=" Current Time is : "+new Date().toLocaleTimeString()
        },1000);


        addEventListener('load',async() => {
            let sw = await navigator.serviceWorker.register('/sw.js');
            console.log(sw);
            console.log('Regsiter with Scope: '+sw.scope);
        });

        function subscribe(){
        	let pukey='';
        	$.ajax({
        		url:'/getPubKey',
        		method:'GET',
        		success:function(data){
        			pukey=data
        			let sw = await navigator.serviceWorker.ready;
                    let push = sw.pushManager.subscribe({
                        userVisibleOnly:true,
                        applicationServerKey:pukey
                    })
                    console.log(push);
                   	console.log(JSON.stringify(push));
                    //Store to database
                    $.ajax({
                    	url:'/subscribe',
                    	method:'POST',
                    	data:JSON.stringify(push),
                    	success:function(data){
                    		console.log(data);
                    	},
                    	fail:function(xhr){
                    		console.log(xhr)
                    	}
                    })
        		},
        		fail:function(xhr){
        			console.log(xhr);
        		}
        	})
        }
    </script>
</body>
</html>
