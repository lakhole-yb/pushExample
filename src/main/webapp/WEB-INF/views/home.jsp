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

        async function subscribe(){
            let sw = await navigator.serviceWorker.ready;
            let push = await sw.pushManager.subscribe({
                userVisibleOnly:true,
                applicationServerKey:'BLid-Ld7O47We3JNKcqnIkgJjQ8sgLpf8BX7FBqtn_dE5tfNWY4h-QzbCdpnPHbVimi2t1Jf3Qvysq-6e-hiotQ'
            });
           
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
            console.log(JSON.stringify(push,null,2));
        }
    </script>
</body>
</html>
