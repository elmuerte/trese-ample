% main entry

go:-go(5).

go(N):-go(N,300).

go(N,Size):-
  init_queens(N,Size,QSize,WhereY,Frame),
  show(Frame),
  queens(N,Qs),
    draw_queens(N,QSize,Qs,Frame),
    show(Frame),
  dialog('More (y/n)?',0,WhereY,Answer),
  not(eq(Answer,y)),
  destroy(Frame).

% visualisation

init_queens(N,Size,QSize,DialogPosition,Frame):-
  compute('+',Size,20,DialogPosition),
  compute(':',Size,N,QSize1),
  compute('-',QSize1,10,QSize),
  new_frame('N-Queens',N,N,Frame),
  new_color(0,0,1,Blue),set_bg(Frame,Blue),
  resize(Frame,Size,Size).

draw_queens(N,QSize,Qs,Frame):-
  '=..'(Queens,[queens|Qs]),
  remove_all(Frame),
  new_color(1,0,0,Red),
  for(I,1,N),for(J,1,N),
  if(arg(I,Queens,J),
     new_image(Frame,'queen.gif',QSize,QSize,P),
     new_panel(Frame,P)
  ),
  set_bg(P,Red),
  fail.
draw_queens(_,_,_,_).

% queens program

queens(N,Ps):-
  gen_places(N,Ps),
  gen_queens(N,Qs),
  place_queens(Qs,Ps,_,_).

place_queens([],_,_,_).
place_queens([I|Is],Cs,Us,[_|Ds]):-
        place_queens(Is,Cs,[_|Us],Ds),
        place_queen(I,Cs,Us,Ds).

place_queen(I,[I|_],[I|_],[I|_]).
place_queen(I,[_|Cs],[_|Us],[_|Ds]):-
        place_queen(I,Cs,Us,Ds).

gen_places(Max,Ps):-
  findall(_,for(_,1,Max),Ps).

gen_queens(Max,Qs):-
  findall(Q,for(Q,1,Max),Qs).
