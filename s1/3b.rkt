;; The first three lines of this file were inserted by DrRacket. They record metadata
;; about the language level of this file in a form that our tools can easily process.
#reader(lib "htdp-beginner-reader.ss" "lang")((modname setb) (read-case-sensitive #t) (teachpacks ()) (htdp-settings #(#t constructor repeating-decimal #f #t none #f () #f)))
(require 2htdp/image)
(require 2htdp/universe)

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
(define interval2 (make-interval -3 -2))
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
(define ds1 (make-flight 10 (make-interval 5 27) (make-posn 10 14)))
(define ds2 (make-flight 1 (make-interval 15 47) (make-posn 8 8)))
(define ds3 (make-launch 2 (make-interval 3 23)))

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
(check-expect (falling-drone ds1) (make-flight 10 (make-interval 5 27) (make-posn 10 13)))
(check-expect (falling-drone ds2) (make-flight 1 (make-interval 15 47) (make-posn 8 7)))
(check-expect (falling-drone ds3) (make-launch 2 (make-interval 3 23)))


; launch-drone : DS -> DS
; If a drone is not in flight, creates a drone at 20 pixels in the air and 15 to the right, ...
; ... otherwise does nothing.  
(define (launch-drone ds)
  (cond
    [(flight? ds) ds]
    [(launch? ds) (make-flight (launch-photographer ds) (launch-goal ds) (make-posn 15 20))]))
; tests
(check-expect (launch-drone ds1) (make-flight 10 (make-interval 5 27) (make-posn 10 14)))
(check-expect (launch-drone ds2) (make-flight 1 (make-interval 15 47) (make-posn 8 8)))
(check-expect (launch-drone ds3) (make-flight 2 (make-interval 3 23) (make-posn 15 20)))


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
(define ds1-shootcomplete (make-flight 1 (make-interval 0 10) (make-posn 5 10)))
(define ds-land (make-flight 10 (make-interval 5 27) (make-posn 10 0)))
(check-expect (shoot-over? ds-land) #true)
(check-expect (shoot-over? ds1-shootcomplete) #true)
(check-expect (shoot-over? ds2) #false)
(check-expect (shoot-over? ds3) #false)


; the constants from which the drone shoot is drawn.
(define ds-scene (empty-scene 250 150))
(define ground (rectangle (image-width ds-scene) 10 "solid" "green"))
(define photographer (rectangle 15 30 "solid" "lightblue"))
(define drone (rectangle 20 15 "solid" "black"))


; place-image-localized-axes : Image Posn Image -> Image
; Uses inverted axes before calling place-image, also ensures that image placement is image size ...
; ... dependent.  Helper method to draw-ds
(define (place-image-inverted-axes image posn scene)
  (place-image
   image
   (+ (posn-x posn) (/ (image-width image) 2))
   (- (-
       (- (image-height ds-scene) (image-height ground))
       (posn-y posn)) (/ (image-height image) 2))
   scene))


; draw-ds : DS -> Image
; draws the drone shoot scene.  Positions calculated relative to scene size to facilitate easy ...
; ... resizing.
(define (draw-ds ds)
  (cond
    [(flight? ds)
     (place-image-inverted-axes
      drone
      (flight-drone ds)
      (place-image-inverted-axes
       photographer
       (make-posn (flight-photographer ds) 0)
       (place-image-inverted-axes
        (rectangle
         (- (interval-right (flight-goal ds))
            (interval-left (flight-goal ds)))
         10 "solid" "orange")
        (make-posn (interval-left (flight-goal ds)) 0)
        (place-image-inverted-axes ground (make-posn 0 -10) ds-scene))))]
    [(launch? ds)
     (place-image-inverted-axes
      photographer
      (make-posn (launch-photographer ds) 0)
      (place-image-inverted-axes
       (rectangle
        (- (interval-right (launch-goal ds))
           (interval-left (launch-goal ds)))
        10 "solid" "orange")
       (make-posn (interval-left (launch-goal ds)) 0)
       (place-image-inverted-axes ground (make-posn 0 -10) ds-scene)))]))
; tests
(define ds1-draw (make-flight 0 (make-interval 5 27) (make-posn 170 10)))
(define ds2-draw (make-flight 150 (make-interval 12 35) (make-posn 20 90)))
(define ds3-draw (make-flight 120 (make-interval 90 140) (make-posn 10 100)))
(define ds4-draw (make-launch 90 (make-interval 40 75)))
(draw-ds ds1-draw)
(draw-ds ds2-draw)
(draw-ds ds3-draw)
(draw-ds ds4-draw)


; control-ds : DS KeyEvent -> DS
; allows keyevent control for the drone shoot.  
(define (control-ds ds keyevent)
  (cond
    
    [(flight? ds)
     (cond
       [(string=? keyevent "up")
        (make-flight
         (flight-photographer ds)
         (flight-goal ds)
         (make-posn (posn-x (flight-drone ds)) (+ (posn-y (flight-drone ds)) 5)))]
       [(string=? keyevent "down") 
        (make-flight
         (flight-photographer ds)
         (flight-goal ds)
         (make-posn (posn-x (flight-drone ds)) (- (posn-y (flight-drone ds)) 5)))]
       [(string=? keyevent "left")
        (make-flight
         (flight-photographer ds)
         (flight-goal ds)
         (make-posn (- (posn-x (flight-drone ds)) 5) (posn-y (flight-drone ds))))]
       [(string=? keyevent "right")
        (make-flight
         (flight-photographer ds)
         (flight-goal ds)
         (make-posn (+ (posn-x (flight-drone ds)) 5) (posn-y (flight-drone ds))))])]
    
    [(launch? ds)
     (cond
       [(string=? keyevent "l")
        (make-flight
         (launch-photographer ds)
         (launch-goal ds)
         (make-posn (+ (launch-photographer ds) 15) 20))])]))


; big-bang
(big-bang ds3-draw
  [on-tick falling-drone .1]
  [on-key control-ds]
  [to-draw draw-ds]
  [stop-when shoot-over?])