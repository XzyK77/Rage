class Vec2i{

  int x, y;
  Vec2i(int _x, int _y){ x = _x; y = _y; }

}

class Vec2f{

  float x, y;
  Vec2f(float _x, float _y){ x = _x; y = _y; }
  
  void sum(float _x, float _y){ x += _x; y += _y; }

}

class Vec3f{

  float x, y, z;
  Vec3f(float _x, float _y, float _z){ x = _x; y = _y; z = _z; }
  
  Vec3f(Vec2f _loc, float _z){ this(_loc.x, _loc.y, _z); }
  
}

class Vec3i{

  int x, y, z;
  Vec3i(int _x, int _y, int _z){ x = _x; y = _y; z = _z; }

}

class Vec4f{
  
  float x, y, w, h;
  Vec4f(float _x, float _y, float _w, float _h ){ x = _x; y = _y; w = _w; h = _h;}
  
}
