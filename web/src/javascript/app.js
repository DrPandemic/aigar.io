/* Test du draw canvas */

$(function() {
    var points = [], numPoints = 2, i, canvas, context, width, height;
    
    canvas = $("#gameCanvas")[0];
    width = canvas.width;
    height = canvas.height;
    context = canvas.getContext("2d");
    var miniMapCanvas = document.createElement('canvas');
    var contextMiniMap = miniMapCanvas.getContext('2d');
    
    function draw() {
        context.clearRect(0, 0, width, height);
        contextMiniMap.clearRect(0, 0, width, height);
        drawCells();
        drawMiniMap();
        canvas.style.background = "#000";
    }
  
    function drawCells(){
        var i, point, len = points.length;
        for(i = 0; i < len; i += 1) {
            point = points[i];
            context.beginPath();
            context.arc(point.x, point.y, point.radius, 0, Math.PI * 2, false);
            context.fillStyle = "#ed1515";
            context.fill();
        }
    }
    
    function drawMiniMap() {

        //set dimensions
        miniMapCanvas.width = canvas.width;
        miniMapCanvas.height = canvas.height;

        contextMiniMap.rect(0,0,200,150);
        contextMiniMap.fillStyle = 'rgba(58, 58, 58, 0.85)';
        contextMiniMap.fill();

        //apply the old canvas to the new one
        contextMiniMap.drawImage(canvas, 0,0, 200, 150);
        
        //code for rectangle to show which part of the map we are viewing
        //...
        
        context.drawImage(miniMapCanvas, 700,0);
    }

  
    function update() {
        var i, point, len = points.length;
        for(i = 0; i < len; i += 1) {
            point = points[i];
            point.x += point.vx;
            point.y += point.vy;
            if(point.x > width ||
                point.x < 0 ||
                point.y > height ||
                point.y < 0) {
                initPoint(point);
            }
        }
    }
  
  //Temporary function to add cells
  //This function will be replaced by real cells
    function initPoint(p) {
        p.x = Math.random() * 500 +5;
        p.y = Math.random() * 500 +5;
        p.vx = Math.random() * 25 -10;
        p.vy = Math.random() * 25 -10;
        p.radius = Math.random() * 50 + 5;
    }
  
  //Add cell into the array of cells
    function addPoint() {
        var point;
        //this if is temporary for tests
        if(points.length < numPoints) {
            point = {};
            initPoint(point);
            points.push(point);
        }
    }

    setInterval(function() {
        addPoint();
        update();
        draw();

    }, 1000/24);
});