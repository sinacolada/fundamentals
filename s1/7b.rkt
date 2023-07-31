;; The first three lines of this file were inserted by DrRacket. They record metadata
;; about the language level of this file in a form that our tools can easily process.
#reader(lib "htdp-intermediate-lambda-reader.ss" "lang")((modname hw7b) (read-case-sensitive #t) (teachpacks ()) (htdp-settings #(#t constructor repeating-decimal #f #t none #f () #f)))
;=====================================================================================================

; Exercise 2

(define posn-1 (make-posn 1 1))
(define posn-2 (make-posn 0 0))
(define posn-3 (make-posn -5 7.6))
(define posn-4 (make-posn -12.1 -750.69))
(define posn-5 (make-posn 1 -10.96))
(define lop-1 '())
(define lop-2 (list posn-1 posn-2 posn-3 posn-4 posn-5))
(define lop-3 (list posn-4 posn-5))
(define lop-4 (list posn-1 posn-2 posn-3))
(define lop-5 (list posn-2 posn-4 posn-5))

; posn-y-below? : Number [List-of Posn] -> Boolean
; determines if every posn's y coordinate in a list of posns is below a given number
(define (posn-y-below? threshold lop)
  (cond [(empty? lop) #f]
        [else (andmap (λ (p) (< (posn-y p) threshold)) lop)]))
; Tests
(check-expect (posn-y-below? 0 lop-1) #f)
(check-expect (posn-y-below? 0 lop-2) #f)
(check-expect (posn-y-below? 0 lop-3) #t)
(check-expect (posn-y-below? 0 lop-4) #f)
(check-expect (posn-y-below? -10 lop-3) #t)
(check-expect (posn-y-below? -10.5 lop-2) #f)
(check-expect (posn-y-below? 5 lop-2) #f)
(check-expect (posn-y-below? 0.01 lop-5) #t)

;=====================================================================================================

; Exercise 3

(define los-1 '())
(define los-2 (list "hi" "hello world" "howdy"))
(define los-3 (list "" "apple" "banana"))
(define los-4 (list "a b c" "d" " e " "fg  hi"))
(define los-5 (list "123" "456" "789" "numbers"))

; hyphenate : [List-of String] [List-of String] -> [List-of String]
; produces a list of strings with a hyphen in between the corresponding element in the first and
; second strings
; assume input strings are the same length
(define (hyphenate los1 los2)
  (map (λ (s1 s2) (string-append s1 "-" s2)) los1 los2))
; Tests
(check-expect (hyphenate los-1 los-1) '())
(check-expect (hyphenate los-2 los-3) (list "hi-" "hello world-apple" "howdy-banana"))
(check-expect (hyphenate los-4 los-5) (list "a b c-123" "d-456" " e -789" "fg  hi-numbers"))

;=====================================================================================================

; Exercise 4

(define (func-1 x) (+ 5 x))
(define (func-2 x) (- x 10))
(define (func-3 x) (/ x 2))
(define (func-4 x) (expt x 1/3))

; bigger-transformation : Number [Number -> Number] [Number -> Number] -> [Number -> Number]
; result is the function whose output was larger on some input
(define (bigger-transformation f1 f2)
  (λ (x) (max (inexact->exact (f1 x)) (inexact->exact (f2 x)))))
; Tests
(check-expect ((bigger-transformation func-1 func-2) 5) 10)
(check-expect ((bigger-transformation func-2 func-3) -1) -0.5)
(check-expect ((bigger-transformation func-3 func-4) 0) 0)
(check-expect ((bigger-transformation func-3 func-4) 27) 13.5)

