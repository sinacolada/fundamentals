;; The first three lines of this file were inserted by DrRacket. They record metadata
;; about the language level of this file in a form that our tools can easily process.
#reader(lib "htdp-intermediate-lambda-reader.ss" "lang")((modname hw13a) (read-case-sensitive #t) (teachpacks ()) (htdp-settings #(#t constructor repeating-decimal #f #t none #f () #f)))
(define TICKET-PRICE 5)

; a Payment is one of:
; - 'credit
; - 5
; - 10
; - 20

(define p-0 'credit)
(define p-1 5)
(define p-2 10)
(define p-3 20)

; a [List-of Payment] represents a payment list,
; where the first customer is at the head of the list

(define lop-0 '())
(define lop-1 (list p-3 p-2 p-1 p-0))
(define lop-2 (list p-3 p-3 p-3 p-2 p-1 p-1 p-0 p-0 p-1 p-2))
(define lop-3 (list p-2 p-1 p-2 p-0 p-2 p-3 p-3))
(define-struct register [fives tens twenties])

; a Register is a (make-register Number Number Number),
; a (make-register a b c) represents the money in a register,
; where a is the number of 5's in the register,
; b is the number of 10's in the register,
; and c is the number of 20's in the register

; number-tickets-sell : Number [List-of Payment] -> Number
; calculates how many tickets can be sold given n 5's in the cash register
; tickets can be sold as long as change can be made if needed
(define (number-tickets-sell n lop)
  (local [(define INITIAL-REGISTER (make-register n 0 0))
          ; update-register : Register Payment -> Number
          ; adds customer's cash (if used) and subtracts change for a customer's
          ; ticket purchase to update the register's state post-transaction
          (define (update-register reg p)
            (cond [(and (symbol? p)
                        (symbol=? p 'credit))
                   reg]
                  [(and (number? p)
                        (= p 5))
                   (make-register (add1 (register-fives reg))
                                  (register-tens reg)
                                  (register-twenties reg))]
                  [(and (number? p)
                        (= p 10))
                   (make-register (sub1 (register-fives reg))
                                  (add1 (register-tens reg))
                                  (register-twenties reg))]
                  [(and (number? p)
                        (= p 20))
                   (local [(define a-ten-a-five-reg
                             (make-register (sub1 (register-fives reg))
                                            (sub1 (register-tens reg))
                                            (add1 (register-twenties reg))))
                           (define three-fives-reg
                             (make-register (- (register-fives reg) 3)
                                            (register-tens reg)
                                            (add1 (register-twenties reg))))]
                     (if (or (< (register-tens a-ten-a-five-reg) 0)
                             (< (register-fives a-ten-a-five-reg) 0))
                         three-fives-reg
                         a-ten-a-five-reg))]
                  [else (error "Please give valid payment method.")]))
          ; num-tickets-sell-helper : Register [List-of Payment] Number -> Number
          ; ACCUMULATE : counts the total number of tickets sold (i)
          (define (num-tickets-sell-helper reg lop i)
            (cond
              [(empty? lop) i]
              [else
               (local [(define next-register-state (update-register reg (first lop)))]
                 (if (not (or (< (register-fives next-register-state) 0)
                              (< (register-tens next-register-state) 0)
                              (< (register-twenties next-register-state) 0)))
                     (num-tickets-sell-helper next-register-state (rest lop) (add1 i))
                     i))]))]
    (num-tickets-sell-helper INITIAL-REGISTER lop 0)))

(check-expect (number-tickets-sell 10 lop-0) 0)
(check-expect (number-tickets-sell 3 lop-1) 1)
(check-expect (number-tickets-sell 11 lop-2) 10)
(check-expect (number-tickets-sell 3 lop-3) 6) 
(check-error (number-tickets-sell 10 (list 'credit 'EBT)) "Please give valid payment method.")


; a NonEmpty-ListOfNumber (NELON) is one of:
; - (cons Number '())
; - (cons Number NELON)

; nth-smallest : Number NELON -> Number
; returns the nth smallest element in a list
; assume n is smaller than the length of the list
(define (nth-smallest n nelon)
  (if (= (length nelon) 1)
      (first nelon)
      (local [(define median-partitioned-list (exact-medians (sort-lonelon (partition-five nelon))))
              (define median-medians (list-ref median-partitioned-list
                                               (floor (/ (length median-partitioned-list) 2))))
              (define pivot-lists (partition-around-pivot median-medians nelon))]
        (cond [(= (length (first pivot-lists)) n)
               (first (second pivot-lists))]
              [(> (length (first pivot-lists)) n)
               (nth-smallest n (first pivot-lists))]
              [(< (length (first pivot-lists)) n)
               (nth-smallest (- n (length (first pivot-lists)) 1) (third pivot-lists))]))))
                                                                            
(check-expect (nth-smallest 1000 (list 5)) 5)
(check-expect (nth-smallest 0 (list 1 2 3 4 5 6 7)) 1)
(check-expect (nth-smallest 3 (list 1 2 3 4 5 6 7)) 4)
(check-expect (nth-smallest 2 (list 69 420 16 8 5 0)) 8)
(check-expect (nth-smallest 5 (list 69 420 16 8 5 0)) 420)
(check-expect (nth-smallest 3 (list 49 36 81 25 100)) 81)

; partition : NELON -> [List-of NELON]
; partitions the list into sublists of size 5
; last list may contain fewer elements due to this function's nature
(define (partition-five lelon)
  (local [(define index-list (build-list (length lelon) identity))
          ; partition-five-helper : Number [List-of LELON] -> [List-of NELON]
          ; ACCUMULATE : index of element in list (i)
          (define (partition-five-helper i lolelon)
            (if (= (modulo i 5) 0)
                (append lolelon (list (list (list-ref lelon i))))
                (remove (list-ref lolelon (- (length lolelon) 1))
                        (append lolelon (list (append (list-ref lolelon (- (length lolelon) 1))
                                                      (list (list-ref lelon i))))))))]
    (foldl partition-five-helper
           '()
           index-list)))

(check-expect (partition-five '()) '())
(check-expect (partition-five '(1 2 3 4 5 6 7)) '((1 2 3 4 5) (6 7)))
(check-expect (partition-five '(69 420 666 1337)) '((69 420 666 1337)))
(check-expect (length (partition-five (build-list 1000 identity))) 200)

; exact-medians : [List-of NELON] -> NELON
; finds the middle element (median) of each sorted sublist in the partitioned list
(define (exact-medians sorted-lonelon)
  (map (λ (sorted-nelon) (list-ref sorted-nelon (floor (/ (length sorted-nelon) 2)))) 
       sorted-lonelon))

(check-expect (exact-medians '((1 2 3 4 5) (5 6 7 8 9) (1 2 3 50 69) (1 2 2 3 5) (1)))
              '(3 7 3 2 1))
(check-expect (exact-medians '((1 2 3 4 5) (6 7)))
              '(3 7))

; sort-lonelon : [List-of NELON] -> [List-of NELON]
; sorts each sublist (ascending) in the list of lists
(define (sort-lonelon lonelon)
  (map (λ (nelon) (sort nelon <))
       lonelon))

(check-expect (sort-lonelon '((4 2 1 3 5) (8 7 6 9 5) (50 69 1 3 2) (1 2 5 3 2) (1)))
              '((1 2 3 4 5) (5 6 7 8 9) (1 2 3 50 69) (1 2 2 3 5) (1)))
(check-expect (sort-lonelon '((1 2 3 4 5) (6 7)))
              '((1 2 3 4 5) (6 7)))

; partition-around-pivot : Number NELON -> [List-of NELON]
; partitions the list into everything less than or equal to the pivot (besides the pivot itself),
; the pivot, and everything bigger than the pivot
(define (partition-around-pivot pivot nelon)
  (foldl (λ (n result) (cond
                         [(and (= n pivot)
                               (zero? (length (second result))))
                          (append (list (first result))
                                  (list (list n))
                                  (list (third result)))]
                         [(<= n pivot)
                          (append (list (append (first result) (list n)))
                                  (list (second result))
                                  (list (third result)))]
                         [(> n pivot)
                          (append (list (first result))
                                  (list (second result))
                                  (list (append (third result) (list n))))]))
         (list '() '() '())
         nelon))

(check-expect (partition-around-pivot 5 '(1 2 3 4 5 6)) '((1 2 3 4) (5) (6)))
(check-expect (partition-around-pivot 7 '(1 2 3 4 5 6 7)) '((1 2 3 4 5 6) (7) ()))
(check-expect (partition-around-pivot 1 '(1 2 3 4 5 6 7)) '(() (1) (2 3 4 5 6 7)))
(check-expect (partition-around-pivot 4 '(1 2 3 4 4 4 5 6 7)) '((1 2 3 4 4) (4) (5 6 7)))

; A Circle is a [CircleMessage -> Any]
 
; A CircleMessage is one of:
; - 'center
; - 'radius
; - 'resize
; - 'equal
; and represents a message to circle, requesting either:
; its center (a Posn)
; its radius (a Number)
; how much to addtively change its radius by (a [Number -> Circle])
; whether or not it has the same size and position as another circle (a [Circle -> Boolean])

; new-Circle : Posn Number -> Circle
; produces a Circle when given posn p for the circle's radius
; and a number r for the radius of the circle
(define (new-circle p r)
  (λ (s)
    (cond [(symbol=? s 'center) p]
          [(symbol=? s 'radius) r]
          [(symbol=? s 'resize) (λ (delta-r) (new-circle p (+ r delta-r)))]
          [(symbol=? s 'equal) (λ (c)
                                 (and (= (posn-x (c 'center))
                                         (posn-x p))
                                      (= (posn-y (c 'center))
                                         (posn-y p))
                                      (= (c 'radius)
                                         r)))])))

(define c0 (new-circle (make-posn 10 20) 4))
(define c1 (new-circle (make-posn 10 20) 9))

(check-expect (c0 'radius) 4)
(check-expect (c0 'center) (make-posn 10 20))
(check-expect (((c0 'resize) 10) 'radius) 14)
(check-expect ((c1 'equal) c0) #f)
(check-expect ((((c1 'resize) -5) 'equal) c0) #t)