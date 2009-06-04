See the .pro file for documentation on the available predicates.

== Additional information =============================================

* ISO Compliance 

The prolog engine is more-or-less ISO Prolog compliant. 


* Consulting prolog files

In order to load other prolog files use the following statements in the
user code:

<code>
:-ensure_loaded('path to my file').
</code>

You can not load prolog files from the query line.


* Initialization

The prolog engine supports initialization. Simply use the following
statement in the prolog file:

<code>
:-initialization(my_pred).

my_pred:-
	trace,
	spy(member/2,call).
</code>

This will enable tracing, and put a spy point on the call of the member/2
predicate.

== Known bugs =========================================================

* Sticky tracing

Tracing and spy-point are sticky for the life span of an initialized
prolog environment. This means that once entered in the query or
anywhere in the user code it will remain active until the prolog 
environment is reinitialized. A prolog environment is reinitialized
everytime the user code is (re)consulted. 

The proper behavior of trace/0 should be:
- if it's a top level instruction, enable tracing until it is disabled
  again. So, if it's in the initialization code or entered solitairy in
  the query it should enable it.
- if it's part of a longer query then only enable it for that query.
