# Open Roberta Lab in MyBinder

Attempt to run Open Roberta Lab in MyBinder and proxy it using `jupyter-server-proxy`.

Seems to build okay, and can be started from a notebook by running:

`!cd //openroberta-lab && /openroberta-lab/ora.sh start-from-git`

However, trying to access it via `jupyter-server-proxy` on `http://PATH/proxy/1999` gives an error:

`Errno 99] Cannot assign requested address`

Is the port number too low?

I can't see an obvious way of setting the port that Open Roberta runs on via the command line?
