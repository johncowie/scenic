# scenic

Scenic is a simple thin wrapper around http://github.com/juxt/bidi that translates routes in a routes.rb-ish file to a Bidi routes data structure.  

## Usage

The ```load-routes-from-file``` function expects a file with lines in the format ```<METHOD> <PATH> <ID>```

e.g. 

```
GET   /       home
GET   /login  login-form
POST  /login  login
```

The ID part is a keyword that points a handler function in a map.  The routes created by ```load-routes-from-file``` can be used in conjunction with the ```scenic-handler```, which creates a ring handler.

```
(scenic-handler routes {:home (fn [req] "home page")}) 
;; => when path with :home is matched, then handler corresponding to :home in handler map will be used.
```

## License

Copyright © 2014 John Cowie

Distributed under the Eclipse Public License, the same as Clojure.
