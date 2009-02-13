% creates new applet console

applet_console:-
  applet_console('').

applet_console(Q):-
  get_applet(F),
  console_in(F,Q).

applet_ide:-applet_ide('').

applet_ide(Q):-
  edit,
  applet_console(Q).

console:-console('').

console(Q):-
 new_frame('Jinni Console',1,1,F),
 console_in(F,Q).

% creates a simple IDE in its own frame
ide:-
 new_frame('Jinni IDE',1,3,F),
 ide_in(F,'append(Xs,Ys,[1,2,3])').

% runs IDE in given 1,2 GridBag frame
ide_in(F,Query):-
 edit_in(F,12,24,200,240,_),
 new_console(F,Query,C),
 resize(C,200,240),
 resize(F,420,280),
 show(F).

% creates new console in given container with inital Query
console_in(F,Query):-new_console(F,Query,_).

edit:-edit(50,50).

% creates editor component in new frame positioned at WhereX, WhereY
edit(WhereX,WhereY):-
  new_frame('Jinni Editor Component',1,1,F),
  move(F,WhereX,WhereY),
  edit_in(F),
  show(F).

edit_in(Container):-
 edit_in(Container,12,48,400,300,_).

% adds editor component to Container
edit_in(Container,Rows,Cols,Xsize,Ysize,P):-
  new_panel(Container,P),
  %eq(P,Container),set_layout(P,border),
  new_text(P,'% enter your program here\n',Rows,Cols,T),
  new_button(P,send,
    reconsult_text(T),_
  ),
  % button_tools_in(P,_),
  new_color(1,1,1,White),
  new_color(0,0,1,Blue),
  set_bg(P,Blue),
  set_bg(T,White),set_fg(T,Blue),
  resize(Container,Xsize,Ysize),
  show(Container).

% reconsults text from editor
reconsult_text(T):-
  new_color(0.50,0.50,0.50,Gray),
  get_text(T,InputString),
  reconsult_string(InputString),
  set_bg(T,Gray).

% creates new chat listener frame
new_listener:-
  new_frame(F),
  new_listener_in(F,_).

% adds new chat listener to container
new_listener_in(Container,ListenerArea):-
  new_console(Container,'listen',ListenerArea).

% creates new button with attached Action - a Jinni goal
new_button(Container,Name,Action):-
  new_button(Container,Name,Action,_).

% creates a new frame with ready to layout one component
new_frame(F):-
  % N,M instead 1,1 means ready to layout N,M rectangular components
  new_frame('Jinni Frame',grid(1,1),F).

new_frame(Name,X,Y,F):-
  new_frame(Name,grid(X,Y),F).

% creates a new Panel with flow Layout
new_panel(Parent,Panel):-new_panel(Parent,flow,Panel).

% creates new default size dialog
dialog(Q,A):-dialog(Q,0,0,A).

% creates image panel with size matching the original image size
new_image(Parent,File,ImagePanel):-
  % 0,0 means use default image size
  new_image(Parent,File,0,0,ImagePanel).

% adds to the end of this container
add_to(Container,Component):-
  add_to(Container,Component,-1).

% button library - to be extended

% creates new button with attached Action - a Jinni goal
button_tools:-
  new_frame(F),
  button_tools_in(F,_).

button_tools_in(C,[B1]):-
  chat_button(C,B1),
  show(C).

chat_button(C,B1):-
  println('testing server'),
  if(remote_run(true),
     new_button(C,'Chat',and(println('type end_of_file to stop'),and(bg(listen),talk)),B1),
     true
  ).

