Session State
============
I implemented a servlet that would maintain
and display a small session state, with session timeout y using the standard technique of using
the standard technique of passing a cookie back and forth between client and server. Also, a
daemon thread was implemented to execute session state cleanup to prevent the session table
from growing very huge. Concurrency conditions were implemented by achieving locks on
objects at session data object level (request from same client) and using doublechecking
technique. The multithreading behavior of container and http servlets was taken into
consideration in the implementation.
