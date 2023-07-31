;; The first three lines of this file were inserted by DrRacket. They record metadata
;; about the language level of this file in a form that our tools can easily process.
#reader(lib "htdp-intermediate-lambda-reader.ss" "lang")((modname hw11b) (read-case-sensitive #t) (teachpacks ()) (htdp-settings #(#t constructor repeating-decimal #f #t none #f () #f)))
(require racket/string)

; A Graph is a (make-graph [List-of Symbol] [Symbol -> [List-of Symbol]])
(define-struct graph [nodes neighbors])
; and represents the nodes and edges in a graph
; All of the symbols in nodes are assumed to be unique, as are the symbols in
; any list returned by neighbors, and all of the symbols returned by neighbors
; are assumed to be in nodes.

(define graph-0 (make-graph '() (λ (s) '())))
(define graph-1 (make-graph '(a b c d e)
                            (λ (s) (cond [(symbol=? s 'a) '(b c)]
                                         [(symbol=? s 'b) '(a c)]
                                         [(symbol=? s 'c) '(a b)]
                                         [(symbol=? s 'd) '(e)]
                                         [(symbol=? s 'e) '(d)]))))
(define graph-2 (make-graph '(a b c d e)
                            (λ (s) (cond [(symbol=? s 'a) '(b e)]
                                         [(symbol=? s 'b) '(a c d e)]
                                         [(symbol=? s 'c) '(d e)]
                                         [(symbol=? s 'd) '(b c e)]
                                         [(symbol=? s 'e) '(a b c)]))))
(define graph-3 (make-graph '(a b c d e f g)
                            (λ (s)(cond [(symbol=? s 'a) '(b c d e f g)]
                                        [(symbol=? s 'b) '(a c d e f g)]
                                        [(symbol=? s 'c) '(a b d e f g)]
                                        [(symbol=? s 'd) '(a b c e f g)]
                                        [(symbol=? s 'e) '(a b c d f g)]
                                        [(symbol=? s 'f) '(a b c d e g)]
                                        [(symbol=? s 'g) '(a b c d e f)]))))
(define graph-4 (make-graph '(e b c d a)
                            (λ (s) (cond [(symbol=? s 'a) '(e b)]
                                         [(symbol=? s 'b) '(c e d a)]
                                         [(symbol=? s 'c) '(e d)]
                                         [(symbol=? s 'd) '(b e c)]
                                         [(symbol=? s 'e) '(b a c)]))))
(define graph-5 (make-graph '(a b c d)
                            (λ (s)(cond [(symbol=? s 'a) '(b c d)]
                                        [(symbol=? s 'b) '()]
                                        [(symbol=? s 'c) '(b d)]
                                        [(symbol=? s 'd) '(b)]))))
(define graph-6 (make-graph '(a b c d e)
                            (λ (s) (cond [(symbol=? s 'a) '(b c d)]
                                         [(symbol=? s 'b) '(c e)]
                                         [(symbol=? s 'c) '()]
                                         [(symbol=? s 'd) '(c e)]
                                         [(symbol=? s 'e) '(a b d)]))))
(define graph-7 (make-graph '(a b c d)
                            (λ (s)(cond [(symbol=? s 'a) '(c)]
                                        [(symbol=? s 'b) '(a d)]
                                        [(symbol=? s 'c) '(a d)]
                                        [(symbol=? s 'd) '(b)]))))

(define graph-8 (make-graph '(a b c)
                            (λ (s) (cond [(symbol=? s 'a) '(b c)]
                                         [(symbol=? s 'b) '(b)]
                                         [(symbol=? s 'c) '(a)]))))
(define graph-9 (make-graph '(a b c d)
                            (λ (s) (cond [(symbol=? s 'a) '(c)]
                                         [(symbol=? s 'b) '(d)]
                                         [(symbol=? s 'c) '(a)]
                                         [(symbol=? s 'd) '(b)]))))
                                         
; neighbor-of? : Graph Symbol Symbol -> Boolean
; determines if the second symbol is a neighbor of the first symbol
(define (neighbor-of? g s1 s2)
  (member? s2 ((graph-neighbors g) s1)))
(check-expect (neighbor-of? graph-0 'a 'b) #false)
(check-expect (neighbor-of? graph-1 'a 'b) #true)
(check-expect (neighbor-of? graph-1 'c 'e) #false)
(check-expect (neighbor-of? graph-1 'd 'e) #true)
(check-expect (neighbor-of? graph-1 'e 'c) #false)
(check-expect (neighbor-of? graph-2 'e 'c) #true)
(check-expect (neighbor-of? graph-2 'c 'a) #false)
(check-expect (neighbor-of? graph-3 'f 'g) #true)
(check-expect (neighbor-of? graph-3 'e 'd) #true)
(check-expect (neighbor-of? graph-3 'g 'f) #true)
(check-expect (neighbor-of? graph-4 'd 'e) #true)
(check-expect (neighbor-of? graph-4 'd 'a) #false)
(check-expect (neighbor-of? graph-5 'a 'd) #true)
(check-expect (neighbor-of? graph-5 'd 'a) #false)
(check-expect (neighbor-of? graph-6 'a 'c) #true)
(check-expect (neighbor-of? graph-6 'c 'a) #false)
(check-expect (neighbor-of? graph-7 'a 'c) #true)
(check-expect (neighbor-of? graph-7 'c 'a) #true)
(check-expect (neighbor-of? graph-7 'd 'a) #false)
(check-expect (neighbor-of? graph-9 'd 'b) #true)

; both-neighbors : Symbol Symbol Graph -> [List-of Symbol]
; returns a list of neighbors for both symbols, with no duplicates
(define (both-neighbors s1 s2 g)
  (append ((graph-neighbors g) s1)
          (filter (λ (n) (not (neighbor-of? g s1 n))) ((graph-neighbors g) s2))))
(check-expect (both-neighbors 'a 'b graph-0) '())
(check-expect (both-neighbors 'a 'd graph-1) '(b c e))
(check-expect (both-neighbors 'b 'c graph-1) '(a c b))
(check-expect (both-neighbors 'd 'e graph-2) '(b c e a))
(check-expect (both-neighbors 'e 'a graph-2) '(a b c e))
(check-expect (both-neighbors 'f 'g graph-3) '(a b c d e g f))
(check-expect (both-neighbors 'g 'f graph-3) '(a b c d e f g))
(check-expect (both-neighbors 'a 'b graph-4) '(e b c d a))
(check-expect (both-neighbors 'b 'd graph-4) '(c e d a b))
(check-expect (both-neighbors 'c 'd graph-5) '(b d))
(check-expect (both-neighbors 'b 'd graph-5) '(b))
(check-expect (both-neighbors 'a 'c graph-6) '(b c d))
(check-expect (both-neighbors 'd 'e graph-6) '(c e a b d))
(check-expect (both-neighbors 'a 'd graph-7) '(c b))
(check-expect (both-neighbors 'b 'c graph-7) '(a d))

; graph=? : Graph Graph -> Boolean
; Determines if two graphs are equal
; (Their nodes have the same names and the same neighbors)
(define (graph=? g1 g2)
  (local [(define g1-nodes (graph-nodes g1))
          (define g2-nodes (graph-nodes g2))]
    (cond
      [(= (length g1-nodes)
          (length g2-nodes))
       (and (andmap (λ (node) (and (member? node (graph-nodes g2))
                                   (andmap (λ (neighbor) (member? neighbor
                                                                  ((graph-neighbors g2) node)))
                                           ((graph-neighbors g1) node))))
                    (graph-nodes g1))
            (andmap (λ (node) (and (member? node (graph-nodes g1))
                                   (andmap (λ (neighbor) (member? neighbor
                                                                  ((graph-neighbors g1) node)))
                                           ((graph-neighbors g2) node))))
                    (graph-nodes g2)))]
      [else #false])))
(check-expect (graph=? graph-0 graph-0) #true)
(check-expect (graph=? graph-0 graph-1) #false)
(check-expect (graph=? graph-2 graph-4) #true)
(check-expect (graph=? graph-2 graph-3) #false)
(check-expect (graph=? graph-3 graph-3) #true)
(check-expect (graph=? graph-0 graph-4) #false)
(check-expect (graph=? graph-5 graph-6) #false)
(check-expect (graph=? graph-5 graph-7) #false)

; graph=?/curried : Graph -> [Graph -> Boolean]
; Curried graph=?
(define graph=?/curried (λ (g1) (λ (g2) (graph=? g1 g2))))
 
; f : Graph ... -> Graph
; Do something to g
#;(define (f g ...) ...)
 
#;(check-satisfied (f some-input-graph ...)
                   (graph=?/curried
                    some-expected-graph))

;; remove-dupes : [List-of X] -> [List-of X]
;; removes any duplicates from a list
(define (remove-dupes lox)
  (cond
    [(empty? lox) empty]
    [(cons? lox) (cons (first lox) (filter (λ (other-x) (not (equal? (first lox) other-x)))
                                           (remove-dupes (rest lox))))]))
(check-expect (remove-dupes '(a b c d a d d b c d e f a b c d)) '(a b c d e f))
(check-expect (remove-dupes '()) '())
(check-expect (remove-dupes '(a b c d)) '(a b c d))
(check-expect (remove-dupes '(1 2 3 3 4)) '(1 2 3 4))

; collapse : Symbol Symbol Symbol Graph -> Graph
; all nodes in the graph pointing to either s1 or s2 now point to s3
; and s3 points to all nodes s1 or s2 pointed to in g
(define (collapse s1 s2 snew g)
  (local [; collapsing-node? : Symbol -> Boolean
          ; Is the node one of the two being collapsed?
          (define (collapsing-node? node)
            (or (symbol=? node s1)
                (symbol=? node s2)))
          ; replace-nodes : [List-of Symbol] -> [List-of Symbol]
          ; replaces two symbols with the new collapsed symbol
          (define (replace-nodes nodes)
            (remove-dupes (map (λ (node) (if (collapsing-node? node)
                                             snew
                                             node))
                               nodes)))
          (define both-nb (replace-nodes (both-neighbors s1 s2 g)))
          (define new-nodes (replace-nodes (graph-nodes g)))]
    (make-graph new-nodes
                (λ (s) (if (symbol=? snew s)
                           both-nb
                           (replace-nodes ((graph-neighbors g) s)))))))
(check-satisfied (collapse 'a 'b 'z graph-1)
                 (graph=?/curried (make-graph '(c d e z)
                                              (λ (s) (cond [(symbol=? s 'z) '(z c)]
                                                           [(symbol=? s 'd) '(e)]
                                                           [(symbol=? s 'e) '(d)]
                                                           [(symbol=? s 'c) '(z)])))))
(check-satisfied (collapse 'e 'b 'z graph-2)
                 (graph=?/curried (make-graph '(a c d z)
                                              (λ (s) (cond [(symbol=? s 'c) '(d z)]
                                                           [(symbol=? s 'd) '(c z)]
                                                           [(symbol=? s 'z) '(a c d z)]
                                                           [(symbol=? s 'a) '(z)])))))
(check-satisfied (collapse 'f 'g 'z graph-3)
                 (graph=?/curried (make-graph '(a b c d e z)
                                              (λ (s)(cond [(symbol=? s 'a) '(b c d e z)]
                                                          [(symbol=? s 'b) '(a c d e z)]
                                                          [(symbol=? s 'c) '(a b d e z)]
                                                          [(symbol=? s 'd) '(a b c e z)]
                                                          [(symbol=? s 'e) '(a b c d z)]
                                                          [(symbol=? s 'z) '(a b c d e z)])))))
(check-satisfied (collapse 'c 'd 'x graph-4)
                 (graph=?/curried (make-graph '(e b a x)
                                              (λ (s) (cond [(symbol=? s 'a) '(e b)]
                                                           [(symbol=? s 'b) '(e a x)]
                                                           [(symbol=? s 'e) '(b a x)]
                                                           [(symbol=? s 'x) '(b e x)])))))
(check-satisfied (collapse 'b 'd 'y graph-5)
                 (graph=?/curried (make-graph '(a c y)
                                              (λ (s)(cond [(symbol=? s 'a) '(c y)]
                                                          [(symbol=? s 'c) '(y)]
                                                          [(symbol=? s 'y) '(y)])))))
(check-satisfied (collapse 'c 'a 'y graph-6)
                 (graph=?/curried (make-graph '(b d e y)
                                              (λ (s) (cond [(symbol=? s 'b) '(e y)]
                                                           [(symbol=? s 'd) '(e y)]
                                                           [(symbol=? s 'e) '(b d y)]
                                                           [(symbol=? s 'y) '(b d y)])))))
(check-satisfied (collapse 'b 'c 'x graph-7)
                 (graph=?/curried (make-graph '(a d x)
                                              (λ (s)(cond [(symbol=? s 'a) '(x)]
                                                          [(symbol=? s 'd) '(x)]
                                                          [(symbol=? s 'x) '(a d)])))))

; reverse-edges : Graph -> Graph
; reverses the edges of a graph
(define (reverse-edges g)
  (local [; nodes-where-neighbor : Symbol -> [List-of-Symbol]
          ; returns list of nodes where symbol s is a neighbor in the graph
          (define (nodes-where-neighbor s)
            (filter (λ (n) (member? s ((graph-neighbors g) n)))
                    (graph-nodes g)))]
    (make-graph (graph-nodes g)
                (λ (s) (nodes-where-neighbor s)))))
(check-satisfied (reverse-edges graph-0)
                 (graph=?/curried (make-graph '() (λ (s) '()))))
(check-satisfied (reverse-edges graph-1)
                 (graph=?/curried graph-1))
(check-satisfied (reverse-edges graph-2)
                 (graph=?/curried (make-graph '(a b c d e)
                                              (λ (s) (cond [(symbol=? s 'a) '(b e)]
                                                           [(symbol=? s 'b) '(a d e)]
                                                           [(symbol=? s 'c) '(b d e)]
                                                           [(symbol=? s 'd) '(b c)]
                                                           [(symbol=? s 'e) '(a b c d)])))))
(check-satisfied (reverse-edges graph-3)
                 (graph=?/curried graph-3))
(check-satisfied (reverse-edges graph-4)
                 (graph=?/curried (make-graph '(e b c d a)
                                              (λ (s) (cond [(symbol=? s 'a) '(b e)]
                                                           [(symbol=? s 'b) '(a d e)]
                                                           [(symbol=? s 'c) '(b d e)]
                                                           [(symbol=? s 'd) '(b c)]
                                                           [(symbol=? s 'e) '(a b c d)])))))
(check-satisfied (reverse-edges graph-5)
                 (graph=?/curried (make-graph '(a b c d)
                                              (λ (s)(cond [(symbol=? s 'a) '()]
                                                          [(symbol=? s 'b) '(a c d)]
                                                          [(symbol=? s 'c) '(a)]
                                                          [(symbol=? s 'd) '(a c)])))))
(check-satisfied (reverse-edges graph-6)
                 (graph=?/curried (make-graph '(a b c d e)
                                              (λ (s) (cond [(symbol=? s 'a) '(e)]
                                                           [(symbol=? s 'b) '(a e)]
                                                           [(symbol=? s 'c) '(a b d)]
                                                           [(symbol=? s 'd) '(a e)]
                                                           [(symbol=? s 'e) '(b d)])))))
(check-satisfied (reverse-edges graph-7)
                 (graph=?/curried (make-graph '(a b c d)
                                              (λ (s)(cond [(symbol=? s 'a) '(b c)]
                                                          [(symbol=? s 'b) '(d)]
                                                          [(symbol=? s 'c) '(a)]
                                                          [(symbol=? s 'd) '(b c)])))))

; rename : Graph [List-of Symbol] -> Graph
; renames a graphs nodes to names in list of symbols the same length as node list
; no duplicates and nth node is renmaed to nth symbol in the given list
(define (rename g los)
  (local [; find-index : [List-of Symbol] Symbol Nat -> Nat
          ; finds the Nat (depending on pass)-based index of an element in a list
          (define (find-index ls z i)
            (cond [(equal? (first ls) z) i]
                  [else (find-index (rest ls) z (add1 i))]))
          ; rename-neighbors : Symbol -> [List-of Symbol]
          ; renames neighbor symbols of s to new ones in gives los
          (define (rename-neighbors s)
            (local [; old-s : Symbol -> Symbol
                    ; finds the corresponding original symbol in same index position
                    (define (old-s x)
                      (list-ref (graph-nodes g) (find-index los x 0)))]
              (map (λ (n) (list-ref los (find-index (graph-nodes g) n 0)))
                   ((graph-neighbors g) (old-s s)))))]
    (make-graph los
                (λ (s) (rename-neighbors s)))))
(check-satisfied (rename graph-0 '())
                 (graph=?/curried (make-graph '() (λ (s) '()))))
(check-satisfied (rename graph-1 '(v w x y z))
                 (graph=?/curried (make-graph '(v w x y z)
                                              (λ (s) (cond [(symbol=? s 'v) '(w x)]
                                                           [(symbol=? s 'w) '(v x)]
                                                           [(symbol=? s 'x) '(v w)]
                                                           [(symbol=? s 'y) '(z)]
                                                           [(symbol=? s 'z) '(y)])))))
(check-satisfied (rename graph-2 '(i j k l m))
                 (graph=?/curried (make-graph '(i j k l m)
                                              (λ (s) (cond [(symbol=? s 'i) '(j m)]
                                                           [(symbol=? s 'j) '(i k l m)]
                                                           [(symbol=? s 'k) '(l m)]
                                                           [(symbol=? s 'l) '(j k m)]
                                                           [(symbol=? s 'm) '(i j k)])))))
(check-satisfied (rename graph-3 '(t u v w x y z))
                 (graph=?/curried (make-graph '(t u v w x y z)
                                              (λ (s)(cond [(symbol=? s 't) '(u v w x y z)]
                                                          [(symbol=? s 'u) '(t v w x y z)]
                                                          [(symbol=? s 'v) '(t u w x y z)]
                                                          [(symbol=? s 'w) '(t u v x y z)]
                                                          [(symbol=? s 'x) '(t u v w y z)]
                                                          [(symbol=? s 'y) '(t u v w x z)]
                                                          [(symbol=? s 'z) '(t u v w x y)])))))
(check-satisfied (rename graph-4 '(e b c d a))
                 (graph=?/curried graph-4))
(check-satisfied (rename graph-5 '(a b c d))
                 (graph=?/curried graph-5))
(check-satisfied (rename graph-6 '(l m n o p))
                 (graph=?/curried (make-graph '(l m n o p)
                                              (λ (s) (cond [(symbol=? s 'l) '(m n o)]
                                                           [(symbol=? s 'm) '(n p)]
                                                           [(symbol=? s 'n) '()]
                                                           [(symbol=? s 'o) '(n p)]
                                                           [(symbol=? s 'p) '(l m o)])))))
(check-satisfied (rename graph-7 '(l m a o))
                 (graph=?/curried (make-graph '(l m a o)
                                              (λ (s)(cond [(symbol=? s 'l) '(a)]
                                                          [(symbol=? s 'm) '(l o)]
                                                          [(symbol=? s 'a) '(l o)]
                                                          [(symbol=? s 'o) '(m)])))))

; numbers->node-name : (list Symbol Symbol) -> Symbol
; Convert a list of symbols of the form (list s1 s2) to 's1->s2
(define (numbers->node-name s)
  (string->symbol (string-append (symbol->string (first s)) "->" (symbol->string (second s)))))
(check-expect (numbers->node-name '(|0| |3|)) '0->3)


; node-name->numbers : Symbol -> (list Nat Nat)
; Convert a symbol of the form 'n1->n2 to (list n1 n2)
(define (node-name->numbers s)
  (map string->number (string-split (symbol->string s) "->")))
(check-expect (node-name->numbers '0->3) '(0 3))

; swap : Graph -> Graph
; swaps a graph's nodes with its edges
(define (swap g)
  (local [(define s-to-num-graph (rename g (build-list (length (graph-nodes g))
                                                       (compose string->symbol number->string))))
          (define num-converted-nodes (graph-nodes s-to-num-graph))
          ; edge-nodes : [List-of Number] -> [List of Symbol]
          ; Converts edges to new nodes
          (define (edge-nodes lon)
            (foldr (λ (n rest)
                     (append (map (λ (nb) (numbers->node-name (list n nb)))
                                  ((graph-neighbors s-to-num-graph) n))
                             rest))
                   '()
                   lon))
          (define new-conv-nodes (edge-nodes num-converted-nodes))
          ; node-edges : Symbol -> [List-of Symbol]
          ; make the new neighbors for nodes based on old nodes
          (define (node-edges s)
            (filter (λ (n) (= (first (node-name->numbers n)) (second (node-name->numbers s))))
                    new-conv-nodes))]  
    (make-graph new-conv-nodes
                (λ (s) (node-edges s)))))
(check-satisfied (swap graph-8)
                 (graph=?/curried (make-graph '(0->1 0->2 1->1 2->0)
                                              (λ (s) (cond [(symbol=? s '0->1) '(1->1)]
                                                           [(symbol=? s '0->2) '(2->0)]
                                                           [(symbol=? s '1->1) '(1->1)]
                                                           [(symbol=? s '2->0) '(0->1 0->2)])))))
(check-satisfied (swap graph-7)
                 (graph=?/curried (make-graph '(0->2 1->0 1->3 2->0 2->3 3->1)
                                              (λ (s) (cond [(symbol=? s '0->2) '(2->0 2->3)]
                                                           [(symbol=? s '1->0) '(0->2)]
                                                           [(symbol=? s '1->3) '(3->1)]
                                                           [(symbol=? s '2->0) '(0->2)]
                                                           [(symbol=? s '2->3) '(3->1)]
                                                           [(symbol=? s '3->1) '(1->0 1->3)])))))

; close? : Graph Symbol Symbols Nat -> Boolean
; is there a path from a node to another in the graph within n steps?
; assuming s1 and s2 are in g
; TERMINATE: we won't revisit a node we've already visited (will never show up twice in a path)
(define (close? g s1 s2 n)
  (local [; close-helper? : Symbol [List-of Symbol] Number -> Boolean
          ; ACCUMULATE: list of visited nodes
          (define (close-helper? s visited count)
            (local [(define neighbors-s ((graph-neighbors g) s))]
              (cond
                [(= n 0) (member? s2 neighbors-s)]
                [(> count n) #false]
                [(member? s visited) #false]
                [(member? s2 neighbors-s) #true]
                [else (ormap (λ (nb) (close-helper? nb
                                                    (cons s visited)
                                                    (add1 count)))
                             neighbors-s)])))]
    (close-helper? s1 '() 1)))
(check-expect (close? graph-1 'a 'c 1) #true)
(check-expect (close? graph-2 'a 'd 1) #false)
(check-expect (close? graph-3 'g 'f 10) #true)
(check-expect (close? graph-4 'b 'd 1) #true)
(check-expect (close? graph-5 'b 'a 3) #false)
(check-expect (close? graph-6 'e 'c 2) #true)
(check-expect (close? graph-7 'a 'b 3) #true)
(check-expect (close? graph-8 'b 'b 0) #true)

; find-all-paths : Symbol Symbol Graph -> [List-of [List-of Symbol]]
; finds all paths between two given nodes in a graph
; if there is a path to itself, the path will be represented as a list of just the node
(define (find-all-paths s1 s2 g)
  (local [; Symbol [List-of-Symbol] -> [List-of [List-of Symbol]]
          ; finds all possible paths between two nodes
          ; ACCUMULATE : list of visited nodes
          (define (find-path-helper s visited)
            (cond
              [(symbol=? s s2) (list (list s2))]
              [(member? s visited) '()] 
              [else (local 
                      [(define s-neighbors ((graph-neighbors g) s))
                       (define neighbor-path-part (find-path-neighbors s-neighbors (cons s visited)))]
                      (map (λ (n) (cons s n)) neighbor-path-part))]))
          ; find-path-neighbors : [List-of Symbol] [List-of Symbol] -> [List-of Symbol]
          ; finds paths from neighbors to end symbol
          (define (find-path-neighbors los visited)  
            (cond [(empty? los) '()]
                  [else (local [(define path (find-path-helper (first los) visited))
                                (define rest-paths (find-path-neighbors (rest los) visited))]
                          (cond [(empty? path) rest-paths]
                                [else 
                                 (append path
                                         rest-paths)]))]))] 
    (find-path-helper s1 '())))
(check-expect (find-all-paths 'a 'b graph-1) '((a b) (a c b)))
(check-expect (find-all-paths 'a 'e graph-2) '((a b c d e)
                                               (a b c e)
                                               (a b d c e)
                                               (a b d e)
                                               (a b e)
                                               (a e)))
(check-expect (find-all-paths 'b 'd graph-4) '((b c d) (b e c d) (b d) (b a e c d)))
(check-expect (find-all-paths 'a 'b graph-5) '((a b) (a c b) (a c d b) (a d b)))
(check-expect (find-all-paths 'c 'e graph-6) '())
(check-expect (find-all-paths 'a 'd graph-7) '((a c d)))
(check-expect (find-all-paths 'b 'b graph-8) '((b)))

; connected? : Graph -> Boolean
; determines if every node in a graph can reach every other node
(define (connected? g)
  (local [(define nodes (graph-nodes g))]
    (andmap (λ (n1) (andmap (λ (n2) (close? g n1 n2 (length nodes)))
                            (remove n1 nodes)))
            nodes)))
(check-satisfied graph-1 (λ (g) (not (connected? g))))
(check-satisfied graph-2 connected?)
(check-satisfied graph-3 connected?)
(check-satisfied graph-4 connected?)
(check-satisfied graph-5 (λ (g) (not (connected? g))))
(check-satisfied graph-6 (λ (g) (not (connected? g))))
(check-satisfied graph-7 connected?)
(check-satisfied graph-8 (λ (g) (not (connected? g))))
(check-satisfied graph-9 (λ (g) (not (connected? g))))

; undirected? : Graph -> Boolean
; determines if every edge in a graph has a matching edge in the opposite direction
(define (undirected? g)
  (andmap (λ (n) (andmap (λ (nb) (close? g nb n 1))
                         ((graph-neighbors g) n)))
          (graph-nodes g)))
(check-satisfied graph-1 undirected?)
(check-satisfied graph-2 (λ (g) (not (undirected? g))))
(check-satisfied graph-3 undirected?)
(check-satisfied graph-4 (λ (g) (not (undirected? g))))
(check-satisfied graph-5 (λ (g) (not (undirected? g))))
(check-satisfied graph-6 (λ (g) (not (undirected? g))))
(check-satisfied graph-7 (λ (g) (not (undirected? g))))
(check-satisfied graph-8 (λ (g) (not (undirected? g))))
(check-satisfied graph-9 undirected?)

(define shape-tester-1 (rename graph-4 '(h d r y s)))
(define shape-tester-2 (make-graph '(b c a e d)
                                   (λ (s) (cond [(symbol=? s 'a) '(e b)]
                                                [(symbol=? s 'b) '(c e d a)]
                                                [(symbol=? s 'c) '(e d)]
                                                [(symbol=? s 'd) '(b e c)]
                                                [(symbol=? s 'e) '(b a c)]))))
(define shape-tester-3 (make-graph '(e b c d a)
                                   (λ (s) (cond [(symbol=? s 'a) '(e b d)]
                                                [(symbol=? s 'b) '(c e d a)]
                                                [(symbol=? s 'c) '(b e d)]
                                                [(symbol=? s 'd) '(b e c)]
                                                [(symbol=? s 'e) '(b a c)]))))

; all-permutations : [List-of X] -> [List-of [List-of X]]
; given a list, outputs all possible element orderings (permutations)
; assuming no duplicate elements
(define (all-permutations lst)
  (cond
    [(= (length lst) 1) (list lst)]
    [else (apply append (map (λ (i) (map (λ (j)(cons i j))
                                         (all-permutations (remove i lst))))
                             lst))]))
(check-expect (all-permutations '()) '())
(check-expect (all-permutations '(1)) '((1)))
(check-expect (all-permutations '(1 2)) '((1 2) (2 1)))
(check-expect (all-permutations '(1 2 3)) '((1 2 3) (1 3 2) (2 1 3) (2 3 1) (3 1 2) (3 2 1)))

; graph-shape=? : Graph Graph -> Boolean
; determines whether two graphs have the same shape
(define (graph-shape=? g1 g2)
  (local ((define namelist (build-list (length (graph-nodes g1)) (λ (x) (identity x))))
          (define graph1 (rename g1 namelist))
          (define permuted-namelist (all-permutations namelist))
          (define permuted-graphs (map (λ (x) (rename g2 x)) permuted-namelist)))
    (if (= (length (graph-nodes g1)) (length (graph-nodes g2)))
        (ormap (λ (y) (graph=? graph1 y)) permuted-graphs)
        #false)))
(check-expect (graph-shape=? graph-4 shape-tester-1) #true)
(check-expect (graph-shape=? graph-4 shape-tester-2) #true)
(check-expect (graph-shape=? graph-4 shape-tester-3) #false)
(check-expect (graph-shape=? graph-2 graph-4) #true)
(check-expect (graph-shape=? graph-6 graph-4) #false)
(check-expect (graph-shape=? graph-0 graph-1) #false)