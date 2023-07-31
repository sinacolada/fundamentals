;; The first three lines of this file were inserted by DrRacket. They record metadata
;; about the language level of this file in a form that our tools can easily process.
#reader(lib "htdp-intermediate-lambda-reader.ss" "lang")((modname hw10a) (read-case-sensitive #t) (teachpacks ()) (htdp-settings #(#t constructor repeating-decimal #f #t none #f () #f)))
; Finger Exercise - Ignore
; add-into : X Nat [List-of X] -> [List-of X]
; adds an item at a specified position in a given list
#|(define (add-into new p lst)
  (cond [(= p 0)
         (cons new lst)]
        [else 
         (cons (first lst) (add-into new (- p 1) (rest lst)))]))

(check-expect (add-into 3 2 '(1 2 4 5 6 7)) '(1 2 3 4 5 6 7))
(check-expect (add-into 3 0 '()) '(3))|#

;=====================================================================================================

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

;=====================================================================================================

(define-struct pair [fst snd])
; a [Pair-of A B] is a (make-pair A B)
 
; A Type is one of:
; - 'number
; - 'boolean
; - 'string
(define-struct pair-ty [fst snd])
; - (make-pair-ty Type Type)
(define-struct fun-ty [arg ret])
; - (make-fun-ty Type Type)
(define-struct list-ty [typ])
; - (make-list-ty Type)
 
; Interpretation: a Type represents different types of data we use in our programs.
; In particular, these are some of the types we write in our signatures.
 
#;(define (type-temp type)
    (cond [(equal? type 'number) ...]
          [(equal? type 'boolean) ...]
          [(equal? type 'string) ...]
          [(pair-ty? type) (... (type-temp (pair-ty-fst type)) ...
                                (type-temp (pair-ty-snd type)) ...)]
          [(fun-ty? type) (... (type-temp (fun-ty-arg type)) ...
                               (type-temp (fun-ty-ret type)) ...)]
          [(list-ty? type) (... (type-temp (list-ty-typ type)) ...)]))
(define Number 'number)
(define Boolean 'boolean)
(define String 'string)
(define (Pair-of A B) (make-pair-ty A B))
(define (Function X Y) (make-fun-ty X Y))
(define (List-of T) (make-list-ty T)) 
 
 
; check : Type X -> X
; ensures the argument x behaves like the type,
; erroring otherwise (either immediately or when used)
(define (check type x)
  (local ((define (err _) (error "the type didn't match: "
                                 x " : " type)))
    (cond [(equal? type 'number) (if (number? x) x (err 1))]
          [(equal? type 'boolean) (if (boolean? x) x (err 1))]
          [(equal? type 'string) (if (string? x) x (err 1))]
          [(pair-ty? type) (if (pair? x)
                               (make-pair
                                (check (pair-ty-fst type) (pair-fst x))
                                (check (pair-ty-snd type) (pair-snd x)))
                               (err 1))]
 
          [(fun-ty? type)
           (if (procedure? x)
               (lambda (y)
                 (local ((define _ (check (fun-ty-arg type) y)))
                   (check (fun-ty-ret type) (x y))))
               (err 1))]
          [(list-ty? type)
           (if (list? x)
               (map (λ (subx) (check (list-ty-typ type) subx)) x)
               (err 1))])))
 
(check-expect (check Number 1) 1)
(check-error (check Number "hi"))
(check-expect (check Boolean #true) #true)
(check-error (check Boolean 2))
(check-expect (check String "hi") "hi")
(check-error (check String 34))
(check-expect (check (Pair-of Number Number) (make-pair 1 2)) (make-pair 1 2))
(check-error (check (Pair-of Number String) 1))
(check-expect ((check (Function Number Number) (lambda (x) x)) 1) 1)
(check-error ((check (Function Number Number) (lambda (x) x)) "hi"))
; identity 'x' in lambda doesn't run b/c function is expecting Number, given String
(check-error ((check (Function Number String) (lambda (x) x)) 1))
(check-error (check (Function Number String) "apple"))
(check-expect (check (List-of Number) '(1 2 3)) '(1 2 3))
(check-error (check (List-of String) '(-1 0 3)))
(check-error (check (List-of String) 5))

#|
; positive : Number -> Boolean
; returns whether number is positive
(define positive (check (Function Number Boolean)
                          (λ (x)
                            (cond
                              [(<= x 0) #false]
                              [(> x 0) #true]))))
(check-expect (positive 5) #true)
(check-expect (positive -5) #false)
|#

; sum-list : [List-of Number] -> Number
; Adds up the numbers in the list
; do not correct the function
(define sum-list (check (Function (List-of Number) Number)
                        (λ (lon) 
                          (cond
                            [(empty? lon) 0]
                            [(cons? lon) (+ (first lon) (sum-list (first lon)))]))))
(check-expect (sum-list '()) 0)
(check-error (sum-list '(1 2 3)) "the type didn't match: 1 : (make-list-ty 'number)")
(check-error (sum-list '(20 50 70 -20 -50 -70)) "the type didn't match: 20 : (make-list-ty 'number)")
(check-error (sum-list "hi"))
(check-error (sum-list 5))
(check-error (sum-list '("hello" "world")))
 
; contains-frog? : [List-of String] -> Boolean
; Returns whether or not the list contains "frog"
; do not correct the function
(define contains-frog? (check (Function (List-of String) Boolean)
                              (λ (los)
                                (cond
                                  [(empty? los) "false"]
                                  [(cons? los) (or (string=? (first los) "frog")
                                                   (contains-frog? (first los)))]))))
(check-error (contains-frog? '()) "the type didn't match: false : 'boolean")
(check-error (contains-frog? '("hello" "world" "frog" "universe"))
             "the type didn't match: hello : (make-list-ty 'string)")
(check-error (contains-frog? '("its" "yo" "boi" "jumpy" "frog"))
             "the type didn't match: its : (make-list-ty 'string)")
(check-error (contains-frog? '("hello" "world" "universe"))
             "the type didn't match: hello : (make-list-ty 'string)")
(check-error (contains-frog? "hi"))
(check-error (contains-frog 5))
(check-error (contains-frog? '(5 7 12 0)))

; type->string : Type -> String
; given a type, will return it as it would appear in a signature
(define (type->string type)
  (cond [(equal? type 'number) "Number"]
        [(equal? type 'boolean) "Boolean"]
        [(equal? type 'string) "String"]
        [(pair-ty? type) (string-append "[Pair-of " (type->string (pair-ty-fst type)) " "
                                        (type->string (pair-ty-snd type)) "]")]
        [(fun-ty? type) (string-append "[" (type->string (fun-ty-arg type)) " -> "
                                       (type->string (fun-ty-ret type)) "]")]
        [(list-ty? type) (string-append "[List-of " (type->string (list-ty-typ type)) "]")]))
(check-expect (type->string Number) "Number")
(check-expect (type->string String)
              "String")
(check-expect (type->string Boolean) "Boolean")
(check-expect (type->string (Pair-of Number Boolean))
              "[Pair-of Number Boolean]")
(check-expect (type->string (Function (Function Number Number) String))
              "[[Number -> Number] -> String]")
(check-expect (type->string (List-of Number)) "[List-of Number]")
(check-expect (type->string (Function (List-of (Pair-of String Number)) Boolean))
              "[[List-of [Pair-of String Number]] -> Boolean]")