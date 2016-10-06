/* Test du draw canvas */

$(function() {
    const points = [];
    const numPoints = 50;
    
    function draw() {
        drawCellsOnMap(points);
        drawMap();
        drawMiniMap();
    }
  
    function update() {
        var i, point, len = points.length;
        for(i = 0; i < len; i += 1) {
            point = points[i];
            point.x += point.vx;
            point.y += point.vy;
            if(point.x > mapWidth ||
                point.x < 0 ||
                point.y > mapHeight ||
                point.y < 0) {
                initPoint(point);
            }
        }
    }
  
  //Temporary function to add cells
  //This function will be replaced by real cells
    function initPoint(p) {
        p.x = Math.random() * 1000 +250;
        p.y = Math.random() * 1000 +250;
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