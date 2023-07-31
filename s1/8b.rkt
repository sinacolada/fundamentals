;; The first three lines of this file were inserted by DrRacket. They record metadata
;; about the language level of this file in a form that our tools can easily process.
#reader(lib "htdp-intermediate-lambda-reader.ss" "lang")((modname |hw8b (1) (1)|) (read-case-sensitive #t) (teachpacks ()) (htdp-settings #(#t constructor repeating-decimal #f #t none #f () #f)))
; CS2500 HW8B

(define-struct node [key info left right])
(define-struct leaf [])

(define LF (make-leaf))

(define node-1 (make-node 1 "a" LF LF))
(define node-2 (make-node 20 "b" LF LF))
(define node-5 (make-node 15 "e" LF node-2))
(define node-6 (make-node 7 "f" LF LF))
(define node-4 (make-node 5 "d" node-1 node-6))

(define node-21 (make-node 5 "y" LF LF))
(define node-22 (make-node 100 "x" LF LF))
(define node-26 (make-node 35 "u" LF LF))
(define node-27 (make-node 65 "t" LF LF))
(define node-24 (make-node 25 "w" node-21 node-26))
(define node-25 (make-node 75 "v" node-27 node-22))

(define node-36 (make-node "k" 0 LF LF))
(define node-31 (make-node "h" 12 LF LF))
(define node-32 (make-node "s" 5 LF LF))
(define node-34 (make-node "i" 7 node-31 node-36))
(define node-35 (make-node "q" 2 LF node-32))

; a Binary Search Tree (BST) is one of:
; - (make-leaf)
; - (make-node X Y BST BST)
; ---------------------------------------
; CONSTRAINT: in (make-node X Y t1 t2)
; all numbers in t1 are __smaller__ than n and
; all numbers in t2 are __greater__ than n

(define bst-0 LF)
(define bst-1 (make-node 10 "c" node-4 node-5))
(define bst-2 (make-node 50 "z" node-24 node-25))
(define bst-3 (make-node "l" 25 node-34 node-35))

; bst-temp : BST -> ???
#;
(define (bst-temp bst)
  (cond [(leaf? bst) ...]
        [(node? bst) ... (bst-key bst)
                     ... (bst-info bst)
                     ... (bst-temp (node-left bst))
                     ... (bst-temp (node-right bst)) ...]))

; a [Comparison X] is a [X X -> Real]
; a negative output indicates the first parameter comes before the second
; a positive output indicates the first parameter comes after the second
; 0 indicates they are equal

(define comparison-1 (λ (x1 x2) (- x2 x1)))
(define comparison-2 (λ (x1 x2) (cond [(> x2 x1) x2]
                                      [(< x2 x1) (* -1 x1)]
                                      [(= x2 x1) 0])))
(define comparison-3 (λ (x1 x2) (cond [(string=? x1 x2) 0]
                                      [(string>? x2 x1) 1]
                                      [(string<? x2 x1) -1])))

(define-struct treemap [compare bst])

; a [TreeMap X Y] is a (make-treemap [Comparison X] BST)

(define treemap-0 (make-treemap comparison-1 bst-0))
(define treemap-1 (make-treemap comparison-1 bst-1))
(define treemap-2 (make-treemap comparison-2 bst-2))
(define treemap-3 (make-treemap comparison-3 bst-3))

; treemap-temp : [TreeMap X Y] -> ???
#;
(define (treemap-temp treemap)
  (... (treemap-compare treemap) ... (bst-temp (treemap-bst treemap)) ...))

; insert : X Y -> [[TreeMap X Y] -> [TreeMap X Y]]
; creates a function that inserts the given key and info into a treemap
; if the key already exists, then the info is overwritten
(define (insert key info)
  (λ (tm) (make-treemap (treemap-compare tm)
                        (insert-into-bst key info (treemap-bst tm) (treemap-compare tm)))))
; Tests
(check-expect (treemap-bst ((insert 5 "e") treemap-0)) (make-node 5
                                                                  "e"
                                                                  LF
                                                                  LF))
(check-expect (treemap-bst ((insert 10 "z") treemap-1)) (make-node 10
                                                                   "z"
                                                                   node-4
                                                                   node-5))
(check-expect (treemap-bst ((insert 25 "b") treemap-2)) (make-node 50
                                                                   "z"
                                                                   (make-node 25
                                                                              "b"
                                                                              node-21
                                                                              node-26)
                                                                   node-25))
(check-expect (treemap-bst ((insert "s" 500) treemap-3)) (make-node "l"
                                                                    25
                                                                    (make-node "i"
                                                                               7
                                                                               (make-node "h"
                                                                                          12
                                                                                          LF
                                                                                          LF)
                                                                               (make-node "k"
                                                                                          0
                                                                                          LF
                                                                                          LF))
                                                                    (make-node "q"
                                                                               2
                                                                               LF
                                                                               (make-node "s"
                                                                                          500
                                                                                          LF
                                                                                          LF)))) 
(check-expect (treemap-bst ((insert 11 "h") treemap-1)) (make-node  10
                                                                    "c"
                                                                    node-4
                                                                    (make-node 15
                                                                               "e"
                                                                               (make-node 11
                                                                                          "h"
                                                                                          LF
                                                                                          LF)
                                                                               node-2)))
(check-expect (treemap-bst ((insert 45 "a") treemap-2))
              (make-node 50
                         "z"
                         (make-node 25
                                    "w"
                                    (make-node 5
                                               "y"
                                               LF
                                               LF)
                                    (make-node 35
                                               "u"
                                               LF
                                               (make-node 45
                                                          "a"
                                                          LF
                                                          LF)))
                         node-25))
(check-expect (treemap-bst ((insert "p" 100) treemap-3)) (make-node "l"
                                                                    25
                                                                    (make-node "i"
                                                                               7
                                                                               (make-node "h"
                                                                                          12
                                                                                          LF
                                                                                          LF)
                                                                               (make-node "k"
                                                                                          0
                                                                                          LF
                                                                                          LF))
                                                                    (make-node "q"
                                                                               2
                                                                               (make-node "p"
                                                                                          100
                                                                                          LF
                                                                                          LF)
                                                                               (make-node "s"
                                                                                          5
                                                                                          LF
                                                                                          LF))))
; insert-into-bst : X Y BST [Comparison-X] -> BST
; inserts new key info pair into the BST
(define (insert-into-bst key info bst comparator)
  (cond [(leaf? bst) (make-node key info LF LF)]
        [(node? bst)
         (cond [(positive? (comparator key (node-key bst)))
                (make-node (node-key bst) (node-info bst)
                           (insert-into-bst key info (node-left bst) comparator)
                           (node-right bst))]
               [(negative? (comparator key (node-key bst)))
                (make-node (node-key bst) (node-info bst)
                           (node-left bst)
                           (insert-into-bst key info (node-right bst) comparator))]
               [(zero? (comparator key (node-key bst)))
                (make-node (node-key bst) info
                           (node-left bst)
                           (node-right bst))])]))
; Tests
(check-expect (insert-into-bst 5 "e" bst-0 comparison-1) (make-node 5 "e" LF LF))
(check-expect (insert-into-bst 10 "z" bst-1 comparison-1) (make-node 10
                                                                     "z"
                                                                     node-4
                                                                     node-5))
(check-expect (insert-into-bst 25 "b" bst-2 comparison-2) (make-node 50
                                                                     "z"
                                                                     (make-node 25
                                                                                "b"
                                                                                node-21
                                                                                node-26)
                                                                     node-25))
(check-expect (insert-into-bst "s" 500 bst-3 comparison-3) (make-node "l"
                                                                      25
                                                                      (make-node "i"
                                                                                 7
                                                                                 (make-node "h"
                                                                                            12
                                                                                            LF
                                                                                            LF)
                                                                                 (make-node "k"
                                                                                            0
                                                                                            LF
                                                                                            LF))
                                                                      (make-node "q"
                                                                                 2
                                                                                 LF
                                                                                 (make-node "s"
                                                                                            500
                                                                                            LF
                                                                                            LF)))) 
(check-expect (insert-into-bst 11 "h" bst-1 comparison-1) (make-node  10
                                                                      "c"
                                                                      node-4
                                                                      (make-node 15
                                                                                 "e"
                                                                                 (make-node 11
                                                                                            "h"
                                                                                            LF
                                                                                            LF)
                                                                                 node-2)))
(check-expect (insert-into-bst 45 "a" bst-2 comparison-2)
              (make-node 50
                         "z"
                         (make-node 25
                                    "w"
                                    (make-node 5
                                               "y"
                                               LF
                                               LF)
                                    (make-node 35
                                               "u"
                                               LF
                                               (make-node 45
                                                          "a"
                                                          LF
                                                          LF)))
                         node-25))
(check-expect (insert-into-bst "p" 100 bst-3 comparison-3) (make-node "l"
                                                                      25
                                                                      (make-node "i"
                                                                                 7
                                                                                 (make-node "h"
                                                                                            12
                                                                                            LF
                                                                                            LF)
                                                                                 (make-node "k"
                                                                                            0
                                                                                            LF
                                                                                            LF))
                                                                      (make-node "q"
                                                                                 2
                                                                                 (make-node "p"
                                                                                            100
                                                                                            LF
                                                                                            LF)
                                                                                 (make-node "s"
                                                                                            5
                                                                                            LF
                                                                                            LF))))

(define ERROR_NO_KEY "No such key is found.")

; find : X -> [[TreeMap X Y] -> Y]
; creates a function that given a key finds the associated info in the tree
; returns an error if no such key is found
(define (find key)
  (λ (tm) (find-key-bst key (treemap-bst tm) (treemap-compare tm))))
; Tests
(check-error ((find 'c) treemap-0) ERROR_NO_KEY)
(check-expect ((find 20) treemap-1) "b")
(check-error ((find 75) treemap-1) ERROR_NO_KEY)
(check-expect ((find 75) treemap-2) "v")
(check-expect ((find "h") treemap-3) 12)
(check-error ((find "a") treemap-3) ERROR_NO_KEY)

; find-key-bst : X BST [Comparison-X] -> Y
(define (find-key-bst key bst comparator)
  (cond [(leaf? bst) (error ERROR_NO_KEY)]
        [(node? bst)
         (cond [(positive? (comparator key (node-key bst)))
                (find-key-bst key (node-left bst) comparator)]
               [(negative? (comparator key (node-key bst)))
                (find-key-bst key (node-right bst) comparator)]
               [(zero? (comparator key (node-key bst)))
                (node-info bst)])]))
; Tests
(check-error (find-key-bst 5 bst-0 comparison-1) ERROR_NO_KEY)
(check-expect (find-key-bst 10 bst-1 comparison-1) "c")
(check-error (find-key-bst 200 bst-1 comparison-1) ERROR_NO_KEY)
(check-expect (find-key-bst 25 bst-2 comparison-2) "w")
(check-expect (find-key-bst "q" bst-3 comparison-3) 2)
(check-error (find-key-bst "a" bst-3 comparison-3) ERROR_NO_KEY)

; submap : X X [TreeMap X Y] -> [TreeMap X Y]
; returns subtreemap of treemap containing only pairs with keys from lo to hi
(define (submap lo hi tm)
  (make-treemap (treemap-compare tm) (create-subtree (treemap-bst tm) lo hi (treemap-compare tm))))
; Tests
(check-expect (treemap-bst (submap 1 100 treemap-0)) bst-0)
(check-expect (treemap-bst (submap 10 10 treemap-1)) (make-node 10 "c" LF LF))
(check-expect (treemap-bst (submap 25 75 treemap-2)) (make-node 50
                                                                "z"
                                                                (make-node 25
                                                                           "w"
                                                                           LF
                                                                           (make-node 35
                                                                                      "u"
                                                                                      LF
                                                                                      LF))
                                                                (make-node 75
                                                                           "v"
                                                                           (make-node 65
                                                                                      "t"
                                                                                      LF
                                                                                      LF)
                                                                           LF)))
(check-expect (treemap-bst (submap 0 1100 treemap-2)) bst-2)
(check-expect (treemap-bst (submap 30 75 treemap-2)) (make-node 50
                                                                "z"
                                             
                                                                (make-node 35
                                                                           "u"
                                                                           LF
                                                                           LF)
                                                                (make-node 75
                                                                           "v"
                                                                           (make-node 65
                                                                                      "t"
                                                                                      LF
                                                                                      LF)
                                                                           LF)))
(check-expect (treemap-bst (submap 30 70 treemap-2)) (make-node 50
                                                                "z"
                                                                (make-node 35
                                                                           "u"
                                                                           LF
                                                                           LF)
                                                                (make-node 65
                                                                           "t"
                                                                           LF
                                                                           LF)))
(check-expect (treemap-bst (submap "i" "q" treemap-3)) (make-node "l"
                                                                  25
                                                                  (make-node "i"
                                                                             7
                                                                             LF
                                                                             (make-node "k"
                                                                                        0
                                                                                        LF
                                                                                        LF))
                                                                  (make-node "q"
                                                                             2
                                                                             LF
                                                                             LF)))
(check-expect (treemap-bst (submap "a" "z" treemap-3)) bst-3)

; create-subtree : BST X X [Comparison-X] -> BST
; makes subtree only containing pairs with keys from lo to hi
(define (create-subtree bst lo hi comparator)
  (cond [(leaf? bst) LF]
        [(node? bst)
         (cond
           [(positive? (comparator hi (node-key bst)))
            (if (leaf? (node-left bst))
                LF
                (create-subtree (node-left bst) lo hi comparator))]
           [(negative? (comparator lo (node-key bst)))
            (if (leaf? (node-right bst))
                LF
                (create-subtree (node-right bst) lo hi comparator))]
           [else
            (make-node (node-key bst)
                       (node-info bst)
                       (create-subtree (node-left bst) lo hi comparator)
                       (create-subtree (node-right bst) lo hi comparator))])]))
; Tests
(check-expect (create-subtree bst-0 0 100 comparison-1) LF)
(check-expect (create-subtree bst-1 10 10 comparison-1) (make-node 10 "c" LF LF))
(check-expect (create-subtree bst-2 25 75 comparison-2) (make-node 50
                                                                   "z"
                                                                   (make-node 25
                                                                              "w"
                                                                              LF
                                                                              (make-node 35
                                                                                         "u"
                                                                                         LF
                                                                                         LF))
                                                                   (make-node 75
                                                                              "v"
                                                                              (make-node 65
                                                                                         "t"
                                                                                         LF
                                                                                         LF)
                                                                              LF)))
(check-expect (create-subtree bst-2 30 75 comparison-2) (make-node 50
                                                                   "z"
                                             
                                                                   (make-node 35
                                                                              "u"
                                                                              LF
                                                                              LF)
                                                                   (make-node 75
                                                                              "v"
                                                                              (make-node 65
                                                                                         "t"
                                                                                         LF
                                                                                         LF)
                                                                              LF)))
(check-expect (create-subtree bst-2 30 70 comparison-2) (make-node 50
                                                                   "z"
                                                                   (make-node 35
                                                                              "u"
                                                                              LF
                                                                              LF)
                                                                   (make-node 65
                                                                              "t"
                                                                              LF
                                                                              LF)))
(check-expect (create-subtree bst-2 0 100 comparison-2) bst-2)
(check-expect (create-subtree bst-3 "i" "q" comparison-3) (make-node "l"
                                                                     25
                                                                     (make-node "i"
                                                                                7
                                                                                LF
                                                                                (make-node "k"
                                                                                           0
                                                                                           LF
                                                                                           LF))
                                                                     (make-node "q"
                                                                                2
                                                                                LF
                                                                                LF)))
(check-expect (create-subtree bst-3 "j" "s" comparison-3) (make-node "l"
                                                                     25
                                                                     
                                                                     (make-node "k"
                                                                                0
                                                                                LF
                                                                                LF)
                                                                     (make-node "q"
                                                                                2
                                                                                LF
                                                                                (make-node "s"
                                                                                           5
                                                                                           LF
                                                                                           LF))))
(check-expect (create-subtree bst-3 "a" "z" comparison-3) bst-3)

; Sexpr Examples
(define sexpr-empty '())
(define sexpr-0 'hello)
(define sexpr-1 (list 'd 'c 'bob 'a))
(define sexpr-2 (list (list 'w 'rad) (list 'app) (list 'b 'c 'not)))
(define sexpr-3 (list 'apple 'b 'c (list 'd 'e 'f) 'g 'hello 'i (list 'j 'k 'l)))

; A [List-of False] can be either
; '()
; [List-of False]
; [List-of [List-of False]]

; interpret : [List-of False] -> [Maybe [List-of Nat]]
; Returns the path to the leftmost occurence of that symbol in the s-expression
(define (interpret l)
  (cond [(empty? l) '()]
        [(cons? l)
         (if (>= (find-num-f l) (length l)) #f
             (if (false? (interpret (list-ref l (find-num-f l)))) #f
                 (cons (find-num-f l) (interpret (list-ref l (find-num-f l))))))]
        [(false? l) #f]))

; find-path : Sexpr Symbol -> [List-of False]
; Returns a list with all unequal symbols mapped to False and the corrext symbol as an empty list
(define (find-path sexpr sym)
  (cond
    [(symbol? sexpr)
     (if (symbol=? sexpr sym)
         '()
         #f)]
    [(list? sexpr)
     (cons (find-path (first sexpr) sym) (los-handler (rest sexpr) sym))]))

; los-handler : List Symbol -> [List-of False]
; Handles cases where Sexpr has a List element
(define (los-handler l sym)
  (cond [(empty? l) '()] 
        [(cons? l)
         (cons (find-path (first l) sym)
               (los-handler (rest l) sym))]))

; find-num-f : List -> Natural
; Determines the number of consecutive Falses in a list
(define (find-num-f l)
  (foldr (λ (x r) (if (false? x)
                      (add1 r)
                      0)) 0 l))
; Tests 
(check-expect (find-num-f (list #f #f #f #f 'a)) 4)
(check-expect (find-num-f (list 'a)) 0)
(check-expect (find-num-f (list)) 0)

; Tests
(check-expect (interpret (find-path (list 'a (list 'a 'b (list 'a 'b 'bob))) 'bob)) '(1 2 2))
(check-expect (interpret (find-path sexpr-0 'hello)) '())
(check-expect (interpret (find-path sexpr-0 'bye)) #f)
(check-expect (interpret (find-path sexpr-1 'bob)) '(2))
(check-expect (interpret (find-path (list 'a (list 'a 'b (list 'a 'b 'bob))) 'bob)) '(1 2 2))
(check-expect (interpret (find-path (list 'a (list 'a 'b (list 'a 'b 'c))) 'bob)) #f)
