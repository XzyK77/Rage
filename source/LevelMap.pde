class LevelMap{                                                                                 //Contains all Segments of level Generation

  Room[] rooms;
  Room[] allRooms;
  
  LevelMap(byte[] _segmentSizes, Vec3i _roomSizeSet, int _roomGap, int _hallSize){              //Will be used to make locations and room sizes. -> int[]_roomSizes, and Vec2f _locs
  
    byte totalSize = 0;                                                                         //Determine total size
    for(int i = 0; i < _segmentSizes.length; i++){ totalSize += _segmentSizes[i]; }
    rooms = new Room[totalSize];
    
    Vec3f roomCandidate = new Vec3f(0.0,0.0,0.0);
    byte roomSizeSetIndex = 0;                                                                  //Keeps track of what roomSizeSet to set roomSize when applying it to roomCandidate
    float roomSize = 0;                                                                         //actual roomSize according to roomSizeSet
    
    for(byte i = 0; i < rooms.length; i++){
      
      switch(roomSizeSetIndex){
      
        case 0: roomSize = _roomSizeSet.x; break;
        case 1: roomSize = _roomSizeSet.y; break;
        case 2: roomSize = _roomSizeSet.z; roomSizeSetIndex = -1; break;
        default:  println("CONSTRUCTOR FAIL:: RoomSegment():: DEFAULT:: roomIndex VALUE = " + roomSizeSetIndex);
      
      }    //save RoomSize for later...
      roomSizeSetIndex++;
      
      boolean isLocationValid = false;           //If declaring variables like "boolean something = false" within a for loop, will that stay in memory till the entire for loop is complete? or dump for each itteration?
      int gap = _roomGap;                        //Gap is re-initialized to original size
      
      while(!(isLocationValid)){
        
        gap += 20;
        if( i - 1 == -1 ){ roomCandidate = new Vec3f(generateRoomLocation(roomSize, new Vec3f(width / 2, height / 2,                  roomSize), gap), roomSize); }      
        else{              roomCandidate = new Vec3f(generateRoomLocation(roomSize, new Vec3f(rooms[i - 1].loc.x, rooms[i - 1].loc.y, roomSize), gap), roomSize); }
        
        isLocationValid = (!(isInsideOtherRooms(roomCandidate, i)));
      }
      rooms[i] = new Room(new Vec2f(roomCandidate.x, roomCandidate.y), roomSize, byte(0), color(255,255,255));
    }
    
    int totalHallRooms = 0;                                                                  //create Corridors
    for(int i = 0; i < rooms.length - 1; i++){                                               //Need total num of rooms based on distance between rooms for array initialization
      
      float a = rooms[i].loc.x - rooms[i + 1].loc.x;                                         //find distance
      float b = rooms[i].loc.y - rooms[i + 1].loc.y;
      float distance = sqrt((a * a) + (b * b));
      totalHallRooms += int(distance / _hallSize);
    }
    
    allRooms = new Room[totalHallRooms + totalSize];
    int allRoomsIndex = rooms.length;                                                        //Start at rooms.length b/c we load rooms into allRooms first
    
    for(int i = 0; i < rooms.length; i++){ allRooms[i] = rooms[i]; }                         //Load rooms into allRooms
    
    for(int i = 0; i < rooms.length - 1; i++){
      
      float a = rooms[i].loc.x - rooms[i + 1].loc.x;
      float b = rooms[i].loc.y - rooms[i + 1].loc.y;
      float distance = sqrt((a * a) + (b * b));
      int numHallRooms = int(distance / _hallSize);
      float subA = a / numHallRooms;
      float subB = b / numHallRooms;
      
      for(int j = 0; j < numHallRooms; j++){
      
        allRooms[allRoomsIndex] = new Room(new Vec2f(rooms[i].loc.x - (subA * j), rooms[i].loc.y - (subB * j)), _hallSize, byte(0), color(255,255,255));
        allRoomsIndex++;
      
      }
    }
  }
  /*==========================================================================================================================================*/
  
  Vec2f generateRoomLocation(float _thisRoomSize, Vec3f _lastRoom, int _gap){                              //Generates Location of new Room
    
    float distance = (_lastRoom.z / 2) + (_thisRoomSize / 2) + _gap;
    float x = random(-distance, distance);
    float y = sqrt((distance * distance) - (x * x));
    int randomY = int(random(2));
    if(randomY == 0){ y = -y; }
    
    return new Vec2f(_lastRoom.x + x, _lastRoom.y + y);
  }
  /*===========================================================================================================================================*/
  
  boolean isInsideOtherRooms(Vec3f _candidate, int _roomIndex){
  
    if(_roomIndex == 0){ return false; }
    
    for(byte i = 0; i < _roomIndex; i++){
      float a = _candidate.y - rooms[i].loc.y;
      float b = _candidate.x - rooms[i].loc.x;
      float distance = sqrt((a * a) + (b * b));
      
      if(distance < ((rooms[i].size / 2) + (_candidate.z / 2))){ return true; }      //Add gap if shit hits the fan with rooms melding into each other.
    }
    return false;
  }
  /*==========================================================================================================================================*/
  
  void display(Vec2f _offset){ for(int i = 1; i < allRooms.length; i++){ allRooms[i].display(_offset); } }
}
