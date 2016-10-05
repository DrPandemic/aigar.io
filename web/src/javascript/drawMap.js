    var screenCanvas = $("#screenCanvas")[0];
    var screenContext = screenCanvas.getContext("2d");
    var screenWidth = screenCanvas.width;
    var screenHeight = screenCanvas.height; 
    //Static position for tests for the screen window on the mini-map
    var xScreenPosOnMap = 200;
    var yScreenPosOnMap = 200;    

    var mapCanvas = document.createElement('canvas');
    var mapContext = mapCanvas.getContext('2d');
    var mapWidth = screenWidth*3;
    var mapHeight = screenHeight*3;

    var miniMapCanvas = document.createElement('canvas');
    var miniMapContext = miniMapCanvas.getContext('2d');
    var miniMapWidth = screenWidth/4;
    var miniMapHeight = screenHeight/4;
    var miniMapPosX = screenWidth-miniMapWidth;

    var miniMapScreenPosWidth = miniMapWidth/3;
    var miniMapScreenPosHeight = miniMapHeight/3;

    function drawCellsOnMap(points){
        //set dimensions
        mapCanvas.width = mapWidth;
        mapCanvas.height = mapHeight;
        
        var i, point, len = points.length;
        for(i = 0; i < len; i += 1) {
            point = points[i];
            
            mapContext.beginPath();
            mapContext.arc(point.x, point.y, point.radius, 0, Math.PI * 2, false);
            mapContext.fillStyle = "#ed1515";
            mapContext.fill();
        }
    }


    function drawMap(){
        screenContext.clearRect(0, 0, screenWidth, screenHeight);
        screenContext.drawImage(mapCanvas, xScreenPosOnMap, yScreenPosOnMap, screenWidth, screenHeight, 0, 0, screenWidth, screenHeight);
    }
    function drawMiniMap() {
        miniMapContext.clearRect(0, 0, screenWidth, screenHeight);
    
        //set dimensions
        miniMapCanvas.width = screenWidth;
        miniMapCanvas.height = screenHeight;

        //MiniMap background
        miniMapContext.rect(0,0,miniMapWidth,miniMapHeight);
        miniMapContext.fillStyle = 'rgba(58, 58, 58, 0.85)';
        miniMapContext.fill();

        //apply the old canvas to the new one
        miniMapContext.drawImage(mapCanvas, 0, 0, miniMapWidth, miniMapHeight);
        
        drawMiniMapminiMapScreenPos();
        
        screenContext.drawImage(miniMapCanvas, miniMapPosX,0);
        screenCanvas.style.background = "#000";
    }

    function drawMiniMapminiMapScreenPos(){
        miniMapContext.strokeStyle="#fff";
        var xMiniMapPos = miniMapWidth/mapWidth*xScreenPosOnMap;
        var yMiniMapPos = miniMapHeight/mapHeight*yScreenPosOnMap;
        miniMapContext.strokeRect(xMiniMapPos,yMiniMapPos,miniMapScreenPosWidth,miniMapScreenPosHeight);
    }