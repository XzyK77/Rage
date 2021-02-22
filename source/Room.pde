class Room{

  Vec2f loc;
  float size;
  color c;
  Block[] allBlocks;
  
  Room(){ this(new Vec2f(0.0,0.0), 10, byte(0), color(255,255,255)); }
  
  Room(Vec2f _loc, float _roomSize, byte _roomType, color _c){
  
    loc = _loc;
    size = _roomSize;
    c = _c; 
    allBlocks = new Block[1];
    
    switch(_roomType){
    
      case 0: break;    //Empty
      case 1: drawRoom1(); break;    
      default:  println("DEFAULT:: CLASS:: ROOM:: TYPE");
    
    }
    
  }
  
  void display(Vec2f _offset){
    
    ellipse(loc.x + _offset.x, loc.y + _offset.y, size, size);
    
  }
  
  void drawRoom1(){
    
    //Ellipse() size, size = width, height so basic drawing area = -1500, 1500 x and y
    Vec2f[] pts = {new Vec2f(-750, 1000), new Vec2f(-500, 100), new Vec2f(500, 100), new Vec2f(750,1000), new Vec2f(1000,1000), new Vec2f(100,-750), new Vec2f(100, -1250), new Vec2f(-100, -1250), new Vec2f(-100,750)};
    
    for(int i = 0; i < pts.length - 1; i++){
    
      //find distance between this point and next point
      
      float a = pts[i].x - pts[i + 1].x;
      float b = pts[i].y - pts[i + 1].y;
      float distance = sqrt((a * a) + (b * b));
      //numofblocks = distance / 30 (blocksize)
      //remainder = distance % 30 (blocksize)
      //numofaddagetoblock = remainder / numofBlocks
      //newblocksize = blocksize + numofaddagetoblock
      //numofblocks = distance / newblocksize
      
      //re initialize array
        //Block[] blocks = new Block[numofblocks];
      
      //create array
      
      
      //after calculations: merge array
        //Block[] temp = newBlock[allBlocks.length + blocks.length];
      
      //2 for loops putting both arraays into it
      //re-assign allBlocks from temp array
      
        //for(int j = 0; j < numofblocks; i++){
      
        
      
      //}
    }
    
    
  
  }

}
