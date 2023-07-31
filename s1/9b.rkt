;; The first three lines of this file were inserted by DrRacket. They record metadata
;; about the language level of this file in a form that our tools can easily process.
#reader(lib "htdp-intermediate-lambda-reader.ss" "lang")((modname hw9b) (read-case-sensitive #t) (teachpacks ()) (htdp-settings #(#t constructor repeating-decimal #f #t none #f () #f)))
; A Bool is a [X X -> X]

; bool-1 : [X X -> X]
; returns the first variable x1
(define bool-1
  (λ (x1 x2) x1))
(check-expect (bool-1 -5 10) -5)
(check-expect (bool-1 "hello" "world") "hello")
(check-expect (bool-1 #true #false) #true)
(check-expect (bool-1 'a 'b) 'a)

; bool-2 : [X X -> X]
; returns the second variable x2
(define bool-2
  (λ (x1 x2) x2))
(check-expect (bool-2 -5 10) 10)
(check-expect (bool-2 "hello" "world") "world")
(check-expect (bool-2 #true #false) #false)
(check-expect (bool-2 'a 'b) 'b)

; Church Booleans
; TRUE := λx.λy.x (first arg, bool-1)
; FALSE := λx.λy.y (second arg, bool-2)

; bool->boolean : Bool -> Boolean : [X X -> X] -> Boolean
; converts to Bool to normal ISL #t/#f
(define (bool->boolean bool)
  (bool #t #f))
(check-expect (bool->boolean bool-1) #true)
(check-expect (bool->boolean bool-2) #false)
  
; AND := λ a . λ b . a b FALSE :=  λ a . λ b . a b (λx.λy.y)
; (a is FALSE, FALSE; a is TRUE, b)

; and/bool : Bool Bool -> Bool : [X X -> X] [X X -> X] -> [X X -> X]
; functions analagously to and
(define (and/bool bool1 bool2)
  (bool1 bool2 bool-2))
(check-expect (bool->boolean (and/bool bool-1 bool-2)) #false)
(check-expect (bool->boolean (and/bool bool-1 bool-1)) #true)
(check-expect (bool->boolean (and/bool bool-2 bool-2)) #false)

; OR := λ a . λ b . a TRUE b := λ a . λ b . a (λx.λy.x) b
; (a is TRUE, TRUE; a is FALSE, b)
              
; or/bool : Bool Bool -> Bool : [X X -> X] [X X -> X] -> [X X -> X]
; functions analagously to or
(define (or/bool bool1 bool2)
  (bool1 bool-1 bool2))
(check-expect (bool->boolean (or/bool bool-1 bool-2)) #true)
(check-expect (bool->boolean (or/bool bool-1 bool-1)) #true)
(check-expect (bool->boolean (or/bool bool-2 bool-2)) #false)

; NOT := λa . a FALSE TRUE := λ a . a (λx.λy.x) (λx.λy.y)
; (p is TRUE, FALSE; p is FALSE, TRUE) 

; not/bool : Bool -> Bool : [X X -> X] -> [X X -> X]
; functions analagously to not
(define (not/bool bool)
  (bool bool-2 bool-1))
(check-expect (bool->boolean (not/bool bool-1)) #false)
(check-expect (bool->boolean (not/bool bool-2)) #true)

; A LeafyTree is one of:
; - 'leaf
; - (make-node LeafyTree LeafyTree)
(define-struct node [l r])

#;(define (lt-temp lt)
    (cond
      [(symbol? lt) ...]
      [(node? lt) ...]))

(define lt-0 'leaf)
(define lt-1 (make-node 'leaf 'leaf))
(define lt-2 (make-node (make-node (make-node 'leaf 'leaf) 'leaf)
                        (make-node (make-node 'leaf 'leaf) 'leaf)))
(define lt-3 (make-node (make-node (make-node 'leaf (make-node (make-node 'leaf 'leaf) 'leaf))
                                   (make-node 'leaf (make-node (make-node 'leaf 'leaf) 'leaf)))
                        'leaf))

; height : LeafyTree -> Nat
; computes the height of a leafy tree
; a leaf has height 0
(define (height lt)
  (cond [(symbol? lt) 0]
        [(node? lt) (add1 (max (height (node-l lt)) (height (node-r lt))))]))
(check-expect (height lt-0) 0)
(check-expect (height lt-1) 1)
(check-expect (height lt-2) 3)
(check-expect (height lt-3) 5)

; all-leafy-trees : Nat -> [List-of LeafyTree]
; returns the list all leafy trees of height n
; a(n+1) = 2*a(n)*(a(0) + ... + a(n-1)) + a(n)^2.

(define (all-leafy-trees n)
  (local[; combine-two : LeafyTree LeafyTree -> LeafyTree
         ; Combines two LeafyTrees into one
         (define (combine-two t1 t2)
           (make-node t1 t2))
         ; combine-to-list : LeafyTree [List-of LeafyTree] -> [List-of LeafyTree]
         ; Combines a given LeafyTree with each element of a list of LeafyTree
         (define (combine-to-list lt lolt)
           (map (λ (x) (combine-two x lt)) lolt))
         ; combine : [List-of LeafyTree] [List-of LeafyTree] -> [List-of LeafyTree]
         ; Generates all possible combinations of trees in the first and second lists
         (define (combine l1 l2)
           (foldr (λ (x lox) (append (combine-to-list x l2) lox)) '() l1))
         ]
    (cond
      [(= 0 n) (cons 'leaf '())]
      [(< 0 n) (append
                (combine (all-leafy-trees (sub1 n))
                         (all-leafy-trees (sub1 n)))
                (combine (all-leafy-trees (sub1 n))
                         (apply append (build-list (sub1 n) all-leafy-trees)))
                (combine (apply append (build-list (sub1 n) all-leafy-trees))
                         (all-leafy-trees (sub1 n))))])))
  
(check-expect (length (all-leafy-trees 0)) 1)
(check-expect (length (all-leafy-trees 1)) 1)
(check-expect (length (all-leafy-trees 2)) 3)
(check-expect (length (all-leafy-trees 3)) 21)
(check-expect (length (all-leafy-trees 4)) 651)
(check-expect (length (all-leafy-trees 5)) 457653)

