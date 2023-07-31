;; The first three lines of this file were inserted by DrRacket. They record metadata
;; about the language level of this file in a form that our tools can easily process.
#reader(lib "htdp-beginner-reader.ss" "lang")((modname seta) (read-case-sensitive #t) (teachpacks ()) (htdp-settings #(#t constructor repeating-decimal #f #t none #f () #f)))
;; Everyday I'm Shuffling

; A CSG (CoinShuffleGame) is a (make-csg CoinOrFalse CoinOrFalse CoinOrFalse)
(define-struct csg [left middle right])
; and represents the three cups in a coin shuffle game, and what is under them
; examples
(define csg1 (make-csg #false #false 3))
(define csg2 (make-csg 2 2 2))
(define csg3 (make-csg 3 1 #false))
; template
(define (csg-temp csg)
  (... (cof-temp csg-left csg1) ...))

; A CoinOrFalse is one of:
; - #false
; - Number
; and represents either no coin or the coin's monetary value
; examples
(define coin-none #false)
(define coin-some 2)
; template
(define (cof-temp cof)
  (cond
    [(boolean? cof) ...]
    [(number? cof) ...]))
 
; A Guess is one of:
; - "left"
; - "middle"
; - "right
; examples
(define guess-left "left")
(define guess-middle "middle")
(define guess-right "right")
; template
(define (guess-temp g)
  (cond
    [(string=? g guess-left) ...]
    [(string=? g guess-middle) ...]
    [(string=? g guess-right) ...]))

; shuffle-right : CSG -> CSG
; Shifts all cup values to the right (right loops left)
(define (shuffle-right csg)
  (make-csg (csg-right csg) (csg-left csg) (csg-middle csg)))
; tests
(define csg1-shuffled (make-csg 3 #false #false))
(define csg2-shuffled (make-csg 2 2 2))
(define csg3-shuffled (make-csg #false 3 1))
(check-expect (shuffle-right csg1) csg1-shuffled)
(check-expect (shuffle-right csg2) csg2-shuffled)
(check-expect (shuffle-right csg3) csg3-shuffled)

; guess-csg : CSG Guess -> CoinOrFalse
; Given a CSG and a Guess, this function outputs the monetary value of the guessed cup.
(define (guess-csg csg guess)
  (cond
    [(string=? guess guess-left) (csg-left csg)]
    [(string=? guess guess-middle) (csg-middle csg)]
    [(string=? guess guess-right) (csg-right csg)]))
; tests
(check-expect (guess-csg csg1 guess-left) #false)
(check-expect (guess-csg csg1 guess-middle) #false)
(check-expect (guess-csg csg1 guess-right) 3)
(check-expect (guess-csg csg3 guess-left) 3)
(check-expect (guess-csg csg3 guess-middle) 1)
(check-expect (guess-csg csg3 guess-right) #false)

; add-if-num : CoinOrFalse Number -> CoinOrFalse
; Via the same method as the CoinOrFalse template, checks value and increments value ...
; ... only if the value is a number.  
(define (add-if-num cof num)
  (cond
    [(boolean? cof) cof]
    [(number? cof) (+ cof num)]))

; inflation : CSG Number -> CSG
; adds value of type "Number" to cups with a value that is not false.  
(define (inflation csg num)
  (make-csg
   (add-if-num (csg-left csg) num)
   (add-if-num (csg-middle csg) num)
   (add-if-num (csg-right csg) num)))
; tests
(define csg1-inflation (make-csg #false #false 5))
(define csg2-inflation (make-csg 4 4 4))
(define csg3-inflation (make-csg 5 3 #false))
(check-expect (inflation csg1 2) csg1-inflation)
(check-expect (inflation csg2 2) csg2-inflation)
(check-expect (inflation csg3 2) csg3-inflation)



;; A Posn Aside

; A Posn is a (make-posn Number Number)
; and represents a 2d coordinate
 
(define POSN-20 (make-posn 20 20))
 
; posn-temp : Posn -> ?
(define (posn-temp p)
  (... (posn-x p) ... (posn-y p)))

; posn-add : Posn Posn -> Posn
; Given two Posns, makes a new Posn with their attributes summed.
(define (posn-add p1 p2)
  (make-posn (+ (posn-x p1) (posn-x p2)) (+ (posn-y p1) (posn-y p2))))
; tests
(check-expect (posn-add (make-posn 20 11) (make-posn -9 9)) (make-posn 11 20))
(check-expect (posn-add (make-posn 10 10) (make-posn 5 5)) (make-posn 15 15))
(check-expect (posn-add (make-posn -1 -1) (make-posn 1 1)) (make-posn 0 0))



;; Drone Shoots
      
; An Interval is a (make-interval Number Number)
(define-struct interval [left right])
; and represents the leftmost and rightmost range of an interval in pixel coordinates (inclusive)

; examples
(define interval1 (make-interval 5 7))
(define interval2 (make-interval -3 -3))
(define interval3 (make-interval 0 0))

; template
(define (interval-temp interval)
  (... (interval-left interval) ... (interval-right interval)))

; A DS (Drone Shoot) is one of:
; - (make-launch Number Interval)
(define-struct launch [photographer goal])
; - (make-flight Number Interval Posn)
(define-struct flight [photographer goal drone])
; Where:
;   - photographer represents a photographer's x-coordinate on the ground
;   - goal represents the range of the desired image to to be captured at the ground-level
;   - drone (if any) represents the drone's x/y position (with 0, 0 at the bottom left)
; All numbers are measured in pixels.

; examples
(define ds1 (make-flight 10 (make-interval 5 7) (make-posn 10 14)))
(define ds2 (make-flight 1 (make-interval 15 27) (make-posn 8 8)))
(define ds3 (make-launch 2 (make-interval 3 3)))

; template
(define (ds-temp ds)
  (cond
    [(flight? ds)
     (...
      (flight-photographer ds) ...
      (interval-temp (flight-interval ds)) ...
      (posn-temp (flight-drone ds)))]
    [(launch? ds)
     (...
      (launch-photographer ds) ...
      (interval-temp (launch-interval ds)))]))

; falling-drone : DS -> DS
; moves a drone (if existent in the Drone Shoot instance) down by one pixel
(define (falling-drone ds)
  (cond
    [(flight? ds)
     (make-flight
      (flight-photographer ds)
      (flight-goal ds)
      (posn-add (flight-drone ds) (make-posn 0 -1)))]
    [(launch? ds) ds]))
; tests
(check-expect (falling-drone ds1) (make-flight 10 (make-interval 5 7) (make-posn 10 13)))
(check-expect (falling-drone ds2) (make-flight 1 (make-interval 15 27) (make-posn 8 7)))
(check-expect (falling-drone ds3) (make-launch 2 (make-interval 3 3)))

; launch-drone : DS -> DS
; If a drone is not in flight, creates a drone at 20 pixels in the air and 15 to the right, ...
; ... otherwise does nothing.  
(define (launch-drone ds)
  (cond
    [(flight? ds) ds]
    [(launch? ds) (make-flight (launch-photographer ds) (launch-goal ds) (make-posn 15 20))]))
; tests
(check-expect (launch-drone ds1) (make-flight 10 (make-interval 5 7) (make-posn 10 14)))
(check-expect (launch-drone ds2) (make-flight 1 (make-interval 15 27) (make-posn 8 8)))
(check-expect (launch-drone ds3) (make-flight 2 (make-interval 3 3) (make-posn 15 20)))

; shoot-over? : DS -> boolean
; checks to see whether the drone shoot is over based on whether the flight goal has been captured...
; ... or the drone has landed.  
(define (shoot-over? ds)
  (cond
    [(flight? ds)
     (or
      (<= (posn-y (flight-drone ds)) 0) ; in which the drone has landed
      (and
       ; the right side is within bounds.  
       (<=
        (interval-right (flight-goal ds))
        (+ (posn-x (flight-drone ds)) (/ (posn-y (flight-drone ds)) 2)))
       ; the left side is within bounds
       (>=
        (interval-left (flight-goal ds)) 
        (- (posn-x (flight-drone ds)) (/ (posn-y (flight-drone ds)) 2)))
       ))]
    [(launch? ds) #false]))
; tests
(define ds-land (make-flight 10 (make-interval 5 7) (make-posn 10 0)))
(check-expect (shoot-over? ds-land) #true)
(check-expect (shoot-over? ds1) #true)
(check-expect (shoot-over? ds2) #false)
(check-expect (shoot-over? ds3) #false)