;; The first three lines of this file were inserted by DrRacket. They record metadata
;; about the language level of this file in a form that our tools can easily process.
#reader(lib "htdp-intermediate-lambda-reader.ss" "lang")((modname |20181202 Homework 14B|) (read-case-sensitive #t) (teachpacks ()) (htdp-settings #(#t constructor repeating-decimal #f #t none #f () #f)))
; 
; StackLang ;;
; 
 
 
; P ::= (i-1,...,i-n)
; i ::= ret v | ret 'x | sub | if0 P P | call | lam x.P
; S ::= (v-1, ..., v-n)
; v ::= n | thunk P
; 
; (P,S) ==> S'
; 
; (i,P,S) ==> S'
 
; A Program is a [List-of Instruction]
 
; An Instruction is a:
; - (list 'push Value)
; - (list 'sub)
; - (list 'mul)
; - (list 'add)
; - (list 'if0 Program Program)
; - (list 'call)
; - (list 'lam 'x Program)
; - (list 'unwrap)
 
; A Stack is a [List-of Value]
 
; A Value is a:
; - Symbol
; - (list 'num Number)
; - (list 'thunk Program)
; - (list 'pair Value Value)
 
; 
; INTERPRETER ;;
; 
 
; interp : Program Stack -> Stack
; runs a program with a given starting stack, producing the final stack or erroring.
(define (interp prog stk)
  (cond
    [(empty? prog) stk]
    [(cons? prog) (interp-instr (first prog) (rest prog) stk)]))
 
(check-expect (interp (list '(push (num 2)))
                      (list))
              (list '(num 2)))
(check-expect (interp (list '(push (num 2)) '(push (num 1)) '(sub))
                      (list))
              (list '(num -1)))
(check-error (interp '((push x))
                     (list)))

(check-expect (interp (list '(push (thunk ((push (num 1))))) '(call))
                      (list))
              (list '(num 1)))
(check-expect (interp (list '(push (num 0)) '(if0 ((push (num 10))) ((push (num 20)))))
                      (list))
              (list '(num 10)))

(define p1 (list '(push (num 1)) '(push (thunk ((lam x ((push x)))))) '(call)))

(check-expect (interp p1 (list))
              (list '(num 1)))

(define p2 (list '(push (num 2)) '(push (num 1)) '(push (num 3)) '(push (thunk ((lam 'x ((sub))))))
                 '(call)))

(check-expect (interp p2 (list))
              (list '(num -1)))
 
; interp-instr : Instruction Program Stack -> Stack
; runs an instruction with a stack and rest of program, producing the final stack or erroring.
(define (interp-instr i prog stk)
  (cond
    [(symbol=? (first i) 'push)
     (if (symbol? (second i))
         (error "Trying to interp a free variable: " (second i))
         (interp prog (cons (second i) stk)))]
    [(symbol=? (first i) 'sub) (interp-sub prog stk)]
    [(symbol=? (first i) 'add) (interp-add prog stk)]
    [(symbol=? (first i) 'mul) (interp-mul prog stk)]
    [(symbol=? (first i) 'if0) (interp-if0 (second i) (third i) prog stk)]
    [(symbol=? (first i) 'call) (interp-call prog stk)]
    [(symbol=? (first i) 'lam) (interp-lam (second i) (third i) prog stk)]
    [(symbol=? (first i) 'unwrap) (interp-unwrap prog stk)]))
 
 
(check-expect (interp-instr '(sub)
                            (list)
                            '((num 1) (num 2)))
              (list '(num -1)))
(check-expect (interp-instr '(push (num 1))
                            (list '(sub))
                            '((num 1)))
              (list '(num 0)))
(check-expect (interp-instr '(push (num 1))
                            (list)
                            '((num 1)))
              '((num 1) (num 1)))

; interp-unwrap : Program Stack -> Stack
; Expects a pair as the first item in the stack
; Unwraps the pair, with the left item being at the top of the stack
(define (interp-unwrap prog stk)
  (cond
    [(empty? stk) (error "The stack is empty.")]
    [(not (and (list? (first stk))
               (symbol=? 'pair (first (first stk)))))
     (error "The first item of the stack is not a pair.")]
    [else (local ((define unwrapped-pair (cons (second (first stk)) (cons (third (first stk)) '())))
                  (define pairless-stk (drop stk)))
            (interp prog (append unwrapped-pair pairless-stk)))]))

(check-expect (interp-unwrap '() '((pair x y) (pair (num 1) (num 2)))) '(x y (pair (num 1) (num 2))))
(check-error (interp-unwrap '((unwrap)) '((pair x y) (pair (num 1) (num 2)))))
(check-error (interp-unwrap '() '()))
 
 
; interp-sub : Program Stack -> Stack
; applies subtraction and then continues running the rest of the program on resulting stack
(define (interp-sub prog stk)
  (cond
    [(not (>= (length stk) 2)) (error "The stack does not have two values to subtract.")]
    [(not (and (list? (first stk))
               (list? (second stk))
               (symbol=? 'num (first (first stk)))
               (symbol=? 'num (first (second stk)))))
     (error "Could not apply subtraction, as the first two values in the stack are not numbers.")]
    [else (local ((define subd-nums (- (second (first stk)) (second (second stk))))
                  (define updated-stk (cons (list 'num subd-nums) (rest (rest stk)))))
            (interp prog updated-stk))]))

(check-expect (interp-sub '() '((num 2) (num 1))) (list '(num 1)))
(check-expect (interp-sub '() '((num 1) (num 2))) (list '(num -1)))
(check-error (interp-sub '() '(x y)))
(check-error (interp-sub '() '((num 1))))
            
; interp-add : Program Stack -> Stack
; applies addition and then continues running the rest of the program on resulting stack
(define (interp-add prog stk)
  (cond
    [(not (>= (length stk) 2)) (error "The stack does not have two values to add.")]
    [(not (and (list? (first stk))
               (list? (second stk))
               (symbol=? 'num (first (first stk)))
               (symbol=? 'num (first (second stk)))))
     (error "Could not apply addition, as the first two values in the stack are not numbers.")]
    [else (local ((define added-nums (+ (second (first stk)) (second (second stk))))
                  (define updated-stk (cons (list 'num added-nums) (rest (rest stk)))))
            (interp prog updated-stk))]))

(check-expect (interp-add '() '((num 2) (num 1))) (list '(num 3)))
(check-expect (interp-add '() '((num 0) (num -1))) (list '(num -1)))
(check-error (interp-add '() '(x y)))
(check-error (interp-add '() '((num 1))))
 
; interp-mul : Program Stack -> Stack
; applies multiplication and then continues running the rest of the program on resulting stack
(define (interp-mul prog stk)
  (cond
    [(not (>= (length stk) 2)) (error "The stack does not have two values to multiply.")]
    [(not (and (list? (first stk))
               (list? (second stk))
               (symbol=? 'num (first (first stk)))
               (symbol=? 'num (first (second stk)))))
     (error "Could not apply multiplication, as the first two values in the stack are not numbers.")]
    [else (local ((define multd-nums (* (second (first stk)) (second (second stk))))
                  (define updated-stk (cons (list 'num multd-nums) (rest (rest stk)))))
            (interp prog updated-stk))]))

(check-expect (interp-mul '() '((num 1) (num 2))) (list '(num 2)))
(check-expect (interp-mul '() '((num 2) (num 2))) (list '(num 4)))
(check-error (interp-mul '() '(x y)))
(check-error (interp-mul '() '((num 1))))

; interp-call : Program Stack -> Stack
; pops a Program off the top of the stack and continues running the program, erroring if no thunk.
(define (interp-call prog stk)
  (cond
    [(< (length stk) 1)
     (error "Could not apply 'call, as the stack was empty.")]
    [(or (not (list? (first stk)))
         (not (equal? 'thunk (first (first stk)))))
     (error "Could not apply 'call, as the top of the stack was not a thunk.")]
    [else (interp (append (second (first stk)) prog)
                  (rest stk))]))
 
(check-expect (interp-call (list)
                           (list (list 'thunk '((push (num 1))))))
              (list '(num 1)))
(check-expect (interp-call (list)
                           (list (list 'thunk '((sub))) '(num 2) '(num 1)))
              (list '(num 1)))
 
; interp-if0 : Program Program Program Stack -> Stack
; pops a number off the stack;
; if number is 0, run thn Program followed by prog on the resulting stack,
; otherwise run els Program and then prog on the resulting stack;
; error if no number on top of stack.
(define (interp-if0 thn els prog stk)
  (cond
    [(not (and (list? (first stk))
               (symbol=? 'num (first (first stk)))))
     (error "There is not a number on the top of the stack.")]
    [(= 0 (second (first stk))) (interp prog (interp thn (rest stk)))]
    [else (interp prog (interp els (rest stk)))]))

(check-expect (interp-if0 '((add)) '((sub)) '((mul)) '((num 0) (num 1) (num 2) (num 3) (num 4)))
              '((num 9) (num 4)))
(check-expect (interp-if0 '((add)) '((sub)) '((mul)) '((num 1) (num 2) (num 3) (num 4) (num 5)))
              '((num -4) (num 5)))
(check-error (interp-if0 '((add)) '((sub)) '((mul)) '(x (num 1) (num 2) (num 3) (num 4))))
 
; —————————————————-
; interp-lam : Symbol Program Program Stack -> Stack
; substitutes into a lambda and stores result on the stack
(define (interp-lam x body prog stk)
  (cond
    [(< (length stk) 1)
     (error "could not apply 'lam, as the stack was empty")]
    [else (interp (append (substitute x (first stk) body) prog)
                  (rest stk))]))
 
(check-expect (interp-lam 'x (list '(push x)) '()
                          (list '(num 1)))
              (list '(num 1)))
 
; substitute : Symbol Value Program -> Program
; substitutes free occurrences of x with v in prog
(define (substitute x v prog)
  (cond [(empty? prog) prog]
        [(cons? prog) (cons (substitute-instr x v (first prog)) (substitute x v (rest prog)))]))

(check-expect (substitute 'x '(num 1) (list '(push x) '(push (num 2))))
              (list '(push (num 1)) '(push (num 2))))
(check-expect (substitute 'x '(num 1) (list '(push x) '(lam x (push x))))
              (list '(push (num 1)) '(lam x (push x))))
(check-expect (substitute 'x '(num 1) (list '(push x) '(unwrap)))
              (list '(push (num 1)) '(unwrap))) 
 
; substitute-instr : Symbol Value Instruction -> Instruction
; substitutes free occurrences of x with v in instruction i
(define (substitute-instr x v i)
  (cond
    [(symbol=? (first i) 'push)
     (list 'push (substitute-val x v (second i)))]
    [(symbol=? (first i) 'add) i]
    [(symbol=? (first i) 'sub) i]
    [(symbol=? (first i) 'mul) i]
    [(symbol=? (first i) 'if0) (list 'if0 (substitute x v (second i))
                                     (substitute x v (third i)))]
    [(symbol=? (first i) 'call) i]
    [(symbol=? (first i) 'lam)
     (if (not (symbol=? (second i) x))
         (list 'lam (second i) (substitute x v (third i)))
         i)]
    [(symbol=? (first i) 'unwrap) i]))
 
(check-expect (substitute-instr 'x '(num 1) '(push x))
              '(push (num 1)))
(check-expect (substitute-instr 'x '(num 1) '(lam x ((push x))))
              '(lam x ((push x))))
(check-expect (substitute-instr 'x '(num 1) '(unwrap))
              '(unwrap))
 
; substitute-val : Symbol Value Value -> Value
; substitutes free occurrences of x with v in original value v0
(define (substitute-val x v v0)
  (cond [(symbol? v0)
         (if (symbol=? x v0) v v0)]
        [(symbol=? (first v0) 'num) v0]
        [(symbol=? (first v0) 'thunk) (list 'thunk (substitute x v (second v0)))]
        [(symbol=? (first v0) 'pair) (list 'pair (substitute-val x v (second v0))
                                           (substitute-val x v (third v0)))]))

(check-expect (substitute-val 'x 'y 'x) 'y)
(check-expect (substitute-val 'x '(num 2) 'x) '(num 2))
(check-expect (substitute-val 'x '(num 1) '(thunk ((lam x ((push x))))))
              '(thunk ((lam x ((push x))))))
(check-expect (substitute-val 'x '(num 1) '(pair (num 2) x)) '(pair (num 2) (num 1)))

; dup : Stack -> Stack
; duplicates the top value on the stack,
; transforming a stack from v1,v2,v3... to v1,v1,v2,v3...
(define (dup s)
  (cond
    [(empty? s) '()]
    [(cons? s) (cons (first s) s)]))

(check-expect (dup '((num 1) x (num 2))) '((num 1) (num 1) x (num 2)))
(check-expect (dup '()) '())
(check-expect (dup '((thunk ((sub1) (add1))))) '((thunk ((sub1) (add1))) (thunk ((sub1) (add1)))))
 
; drop : Stack -> Stack
; drops the top value from the stack,
; transforming a stack from v1,v2,v3... to v2,v3...
(define (drop s)
  (cond
    [(empty? s) '()]
    [(cons? s) (rest s)]))

(check-expect (drop '((num 1) x (num 2))) '(x (num 2)))
(check-expect (drop '()) '())
(check-expect (drop '((thunk ((sub1) (add1))))) '())
 
; swap : Stack -> Stack
; swaps the top two values on the stack,
; transforming a stack from v1,v2,v3... to v2,v1,v3...
(define (swap s)
  (cond
    [(empty? s) '()]
    [(cons? s) (cons (second s) (cons (first s) (drop (drop s))))]))

(check-expect (swap '((num 1) x (num 2))) '(x (num 1) (num 2)))
(check-expect (swap '()) '())
(check-expect (swap '((thunk ((sub1) (add1))) (thunk ((add1) (sub1))))) '((thunk ((add1) (sub1)))
                                                                          (thunk ((sub1) (add1)))))

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

#;(define (expr-temp e)
    (cond
      [(number? e) ...]
      [(boolean? e) ...]
      [(symbol? e) ...]
      [(member? (first e) AOPS) ... (expr-temp (second e)) ... (expr-temp (third e)) ...]
      [(member? (first e) BOPS) ... (expr-temp (second e)) ... (expr-temp (third e)) ...]
      [(member? (first e) CmpOPS) ... (expr-temp (second e)) ... (expr-temp (third e)) ...]
      [(symbol=? (first e) 'if) ... (expr-temp (second e)) ... (expr-temp (third e)) ...
                                (expr-temp (fourth e)) ...]
      [(symbol=? (first e) 'var) ... (second e) ...]
      [(symbol=? (first e) 'lam) ... (second e) ... (third e) ... (expr-temp (fourth e)) ...]
      [(symbol=? (first e) 'app) ... (expr-temp (second e)) ... (expr-temp (third e)) ...]
      [(symbol=? (first e) 'pair) ... (expr-temp (second e)) ... (expr-temp (third e)) ...]
      [(symbol=? (first e) 'fst) ... (expr-temp (second e)) ...]
      [(symbol=? (first e) 'snd) ... (expr-temp (second e)) ...]
      [(symbol=? (first e) 'inleft) ... (expr-temp (second e)) ... (third e) ... (fourth e) ...]
      [(symbol=? (first e) 'inright) ... (expr-temp (second e)) ... (third e) ... (fourth e) ...]
      [(symbol=? (first e) 'case) ... (expr-temp (second e)) ... (third e) ...
                                  (expr-temp (fourth e)) ... (fifth e) ...
                                  (expr-temp (sixth e)) ...]))
 
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

; compile : Expression -> Program
; translates a Simply Typed Expression into a StackLang Program
(define (compile e)
  (cond
    [(number? e) (list (list 'push (list 'num e)))]
    [(symbol? e) (list (list 'push e))]
    [(symbol=? (first e) '+) (append (compile (second e))
                                     (compile (third e))
                                     '((add)))]
    [(symbol=? (first e) '-) (append (compile (third e))
                                     (compile (second e))
                                     '((sub)))]
    [(symbol=? (first e) '*) (append (compile (third e))
                                     (compile (second e))
                                     '((mul)))]
    [(symbol=? (first e) 'inleft) (compile-inleft (second e))]
    [(symbol=? (first e) 'inright) (compile-inright (second e))]
    [(symbol=? (first e) 'case) (compile-case (second e) (third e) (fourth e)
                                              (fifth e) (sixth e))]
    [else (error "The entered expression is invalid.")]))

(check-expect (interp (compile 1) '()) '((num 1)))
(check-expect (interp (compile '(+ 1 2)) '()) '((num 3)))
(check-expect (interp (compile '(- 2 1)) '()) '((num 1)))
(check-expect (interp (compile '(* 2 2)) '()) '((num 4)))
(check-expect (interp (compile '(case (inleft (+ 1 2) Number Boolean) x (+ 1 x) y (+ 2 y))) '())
              '((num 4)))
(check-expect (interp (compile '(inright (+ 1 2) Boolean Number)) '()) '((pair (num 1) (num 3))))
(check-expect (interp (compile '(inleft (+ 1 4) Number Boolean)) '()) '((pair (num 0) (num 5))))
(check-error (compile '(/ 2 1)))

; compile-inleft : Expression -> Program
; translates Expression in an inleft into corresponding StackLang Program
(define (compile-inleft e)
  (append (compile e) '((lam x ((push (pair (num 0) x)))))))

(check-expect (interp (compile-inleft '(+ 1 2)) '()) '((pair (num 0) (num 3))))
(check-expect (interp (compile-inleft '(- 2 1)) '()) '((pair (num 0) (num 1))))

; compile-inright : Expression -> Program
; translates Expression in an inright into corresponding StackLang Program
(define (compile-inright e)
  (append (compile e) '((lam y ((push (pair (num 1) y)))))))

(check-expect (interp (compile-inright '(+ 2 4)) '()) '((pair (num 1) (num 6))))
(check-expect (interp (compile-inright '(* 2 4)) '()) '((pair (num 1) (num 8))))

; compile-case : Expression Symbol Expression Symbol Expression -> Program
; translates Expressions, bindings, and branches of a case into corresponding StackLang Program
(define (compile-case e x e1 y e2)
  (append (compile e) '((unwrap)) (list (list 'if0 (list (list 'lam x (compile e1)))
                                              (list (list 'lam y (compile e2)))))))

(check-expect (interp (compile-case '(inleft (+ 1 4) Number Boolean) 'x '(+ 1 2) 'y '(+ 1 3)) '())
              '((num 3)))
(check-expect (interp (compile-case '(inleft (+ 1 4) Number Boolean) 'x '(+ x 2) 'y '(+ y 3)) '())
              '((num 7)))
(check-expect (interp (compile-case '(inright (+ 1 4) Boolean Number) 'x '(+ 1 2) 'y '(+ 1 3)) '())
              '((num 4)))
(check-expect (interp (compile-case '(inright (+ 1 4) Boolean Number) 'x '(+ x 2) 'y '(+ y 3)) '())
              '((num 8)))
