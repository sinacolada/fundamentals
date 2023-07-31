;; The first three lines of this file were inserted by DrRacket. They record metadata
;; about the language level of this file in a form that our tools can easily process.
#reader(lib "htdp-intermediate-lambda-reader.ss" "lang")((modname |20181127 Homework 13B|) (read-case-sensitive #t) (teachpacks ()) (htdp-settings #(#t constructor repeating-decimal #f #t none #f () #f)))
; SIMPLE TYPED LANGUAGE
 
; An Expression is a:
; - Number
; - Boolean
; - (list AopName Expression Expression)
; - (list BopName Expression Expression)
; - (list CmpopName Expression Expression)
; - (list 'if Expression Expression Expression)
; - (list 'var Symbol)
; - (list 'lam Symbol Type Expression)
; - (list 'app Expression Expression)
; - (list 'pair Expression Expression)
; - (list 'fst Expression)
; - (list 'snd Expression)
; - (list 'inleft Expression Type Type)
; - (list 'inright Expression Type Type)
; - (list 'case Expression Symbol Expression Symbol Expression)
; - 'unit

(define AOPS '(+ -))
; An AopName is a member of AOPS, all of which have type: Number Number -> Number
(define BOPS '(and or))
; An BopName is a member of BOPS, all of which have type: Boolean Boolean -> Boolean
(define CmpOPS '(> < =))
; An CmpopName is a member of CmpOPS, all of which have type: Number Number -> Boolean
 
; A Type is one of:
; - 'Number
; - 'Boolean
(define-struct funty [arg ret])
; - (make-funty Type Type)
(define-struct pair [fst snd])
; - (make-pair Type Type)
(define-struct sum [left right])
; - (make-sum Type Type)
; - 'Unit
 
; ensuretype : Environment Expression Type -> Type
; Check that expression e has type t, error if e's type does not match t
(define (ensuretype env e t)
  (local ((define ty-e (typecheck-env env e)))
    (if (equal? ty-e t)
        t
        (error "Expression " e " has type " ty-e " but was expected to have type " t))))
 
(define-struct var:ty [var ty])
; An Environment is a [List of (make-var:ty Symbol Type)]
; Interp: A mapping from variables to types
 
; typecheck-env : Environment Expresssion -> Type
; return the type of the expression e or error if the expression is not well typed
; Accumulator: env represents each variable and its type
(define (typecheck-env env e)
  (cond [(number? e) 'Number]
        [(boolean? e) 'Boolean]
        [(symbol? e) 'Unit]
        [(member? (first e) AOPS)
         (local ((define t-1 (ensuretype env (second e) 'Number))
                 (define t-2 (ensuretype env (third e) 'Number)))
           'Number)]
        [(member? (first e) BOPS)
         (local ((define t-1 (ensuretype env (second e) 'Boolean))
                 (define t-2 (ensuretype env (third e) 'Boolean)))
           'Boolean)]
        [(member? (first e) CmpOPS)
         (local ((define t-1 (ensuretype env (second e) 'Number))
                 (define t-2 (ensuretype env (third e) 'Number)))
           'Boolean)]
        [(symbol=? (first e) 'if)
         (local ((define t-0 (ensuretype env (second e) 'Boolean))
                 (define ty-e1 (typecheck-env env (third e)))
                 (define ty-e2 (typecheck-env env (fourth e))))
           (if (equal? ty-e1 ty-e2)
               ty-e1
               (error "Branches of if expression " e "have different types")))]
        [(symbol=? (first e) 'var)
         (local ((define filtered (filter (λ (x) (symbol=? (var:ty-var x) (second e))) env)))
           (if (not (empty? filtered))
               (var:ty-ty (first filtered)) 
               (error "The variable is not defined.")))]
        [(symbol=? (first e) 'lam)
         (local ((define var (make-var:ty (second e) (third e))))
           (make-funty (third e) (typecheck-env (cons var env) (fourth e))))]
        [(symbol=? (first e) 'app)
         (local ((define t-1 (typecheck-env env (second e)))
                 (define t-2 (typecheck-env env (third e))))
           (if (funty? t-1)
               (if (equal? (funty-arg t-1) t-2)
                   (funty-ret t-1)
                   (error "The given types are inconsistent."))
               (error "The applied expression is not a function.")))]
        [(symbol=? (first e) 'pair)
         (local ((define t-1 (typecheck-env env (second e)))
                 (define t-2 (typecheck-env env (third e))))
           (make-pair t-1 t-2))]
        [(symbol=? (first e) 'fst)
         (local ((define PAIR (typecheck-env env (second e))))
           (if (pair? PAIR)
               (pair-fst PAIR)
               (error "The given expression is not a pair.")))]
        [(symbol=? (first e) 'snd)
         (local ((define PAIR (typecheck-env env (second e))))
           (if (pair? PAIR)
               (pair-snd PAIR)
               (error "The given expression is not a pair.")))]
        [(symbol=? (first e) 'inleft)
         (local ((define t-1 (ensuretype env (second e) (third e))))
           (make-sum t-1 (fourth e)))]
        [(symbol=? (first e) 'inright)
         (local ((define t-1 (ensuretype env (second e) (fourth e))))
           (make-sum (third e) t-1))]
        [(symbol=? (first e) 'case)
         (local ((define t-0 (typecheck-env env (second e))))
           (if (sum? t-0)
               (local ((define t-1 (typecheck-env (cons (make-var:ty (third e) (sum-left t-0)) env)
                                                  (fourth e)))
                       (define t-2 (typecheck-env (cons (make-var:ty (fifth e) (sum-right t-0)) env)
                                                  (sixth e))))
                 (if (equal? t-1 t-2)
                     t-1
                     (error "The branches have different types.")))
               (error "The first expression's type is not a sum type.")))]))
 
; typecheck : Expression -> Type
; return the type of the expression e or error if the expression is not well typed
(define (typecheck e)
  (typecheck-env '() e))
 
(check-expect (typecheck 1) 'Number)
(check-expect (typecheck #false) 'Boolean)
(check-expect (typecheck (list '+ 1 2)) 'Number)
(check-expect (typecheck (list '- (list '+ 1 2) 5)) 'Number)
(check-error (typecheck (list '+ #false 2)))
(check-error (typecheck (list 'if 1 #true #false)))
(check-error (typecheck '(if (> 3 9) 1 #true)))
(check-expect (typecheck '(if (> 3 9) 1 4)) 'Number)
(check-expect (typecheck '(and #false (> 2 2))) 'Boolean)
(check-error (typecheck '(and #false (+ 2 2))))

(check-expect (typecheck-env (list (make-var:ty 'x 'Symbol)) '(var x)) 'Symbol)
(check-error (typecheck '(var x)))
(check-expect (typecheck (list 'lam 'x 'Number '(> 2 (var x)))) (make-funty 'Number 'Boolean))
(check-error (typecheck (list 'lam 'y 'Boolean '(> 2 (var y)))))
(check-expect (typecheck (list 'app (list 'lam 'x 'Number '(> 2 (var x))) 2)) 'Boolean)
(check-error (typecheck (list 'app (list 'lam 'x 'Number '(+ 2 (var x))) #false)))
(check-error (typecheck (list 'app 2 '(+ 2 (var x)))))
(check-error (typecheck (list 'app (list 'lam 'x 'Number '(> 2 (var x))) #true)))
(check-error (typecheck (list 'app 2 #true)))
(check-expect (typecheck (list 'pair 2 2)) (make-pair 'Number 'Number))
(check-expect (typecheck (list 'pair #true 2)) (make-pair 'Boolean 'Number))
(check-error (typecheck (list 'pair "hello" 2)))
(check-expect (typecheck (list 'fst (list 'pair 1 #true))) 'Number)
(check-error (typecheck (list 'fst 2)))
(check-expect (typecheck (list 'snd (list 'pair 1 #true))) 'Boolean)
(check-error (typecheck (list 'snd #true)))
(check-expect (typecheck (list 'inleft (+ 3 4) 'Number 'Boolean)) (make-sum 'Number 'Boolean))
(check-error (typecheck (list 'inleft (+ 3 4) 'Boolean 'Number)))
(check-expect (typecheck (list 'inright #true 'Number 'Boolean)) (make-sum 'Number 'Boolean))
(check-error (typecheck (list 'inright #true 'Boolean 'Number)))
(check-expect (typecheck (list 'case (list 'inleft 42 'Number 'Boolean) 'x '(+ 2 (var x))
                               'y 2)) 'Number)
(check-error (typecheck (list 'case #true 'x '(+ 2 (var x))
                              'y 2)))
(check-error (typecheck (list 'case (list 'inleft 42 'Number 'Boolean) 'x '(+ 2 (var x))
                              'y #true)))
(check-expect (typecheck 'unit) 'Unit)

; true
(define TRUE (list 'inleft 'unit 'Unit 'Unit))

; false
(define FALSE (list 'inright 'unit 'Unit 'Unit))

; if e0 then e1 else e2
(define IF (list 'case 'e0 'x 'e1 'y 'e2))
