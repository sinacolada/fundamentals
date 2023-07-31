;; The first three lines of this file were inserted by DrRacket. They record metadata
;; about the language level of this file in a form that our tools can easily process.
#reader(lib "htdp-beginner-reader.ss" "lang")((modname wk2setb) (read-case-sensitive #t) (teachpacks ()) (htdp-settings #(#t constructor repeating-decimal #f #t none #f () #f)))
; pounds -> galleons : Number -> Number
; takes pounds and converts to galleons: ...
; ...3 pounds service, 5 pounds per galleon rate

(define SERVICEFEE 3)
(define CONVERSIONRATE 5)

(define (diagon-convert pounds)
  (/ (- pounds SERVICEFEE) CONVERSIONRATE))

(check-expect (diagon-convert 8) 1)
(check-expect (diagon-convert 13) 2)
(check-expect (diagon-convert 20) 3.4)
(check-expect (diagon-convert 50) 9.4)

; word -> bingo word : String -> String
; Takes string, returns first letter capitalized, ...
; ... space, then number of characters in the word

(define (bingo-word word)
  (string-append (string-upcase (substring word 0 1)) " " (number->string (string-length word))))

(check-expect (bingo-word "bingo") "B 5")
(check-expect (bingo-word "Win") "W 3")
(check-expect (bingo-word "sugar") "S 5")

; time -> image "moving" : Number -> image
; takes time to move image in real time ultilizing the ...
; ... universe and image teachpacks producing an animation

(require 2htdp/image)
(require 2htdp/universe)

(define BACKGROUNDRIGHT (rectangle 400 40 "solid" "black"))
(define BACKGROUNDLEFT (rectangle 400 40 "solid" "light blue"))
(define BALL (circle 20 "solid" "red"))
(define BALL_YPOS 20)
(define TIMETURN 360)
(define LEFTBOUND 20)
(define RIGHTBOUND 380)

(define (to-and-fro time)
  (if
   (odd? (quotient time TIMETURN))
   (place-image BALL (- RIGHTBOUND (modulo time TIMETURN)) BALL_YPOS BACKGROUNDLEFT)
   (place-image BALL (+ LEFTBOUND (modulo time TIMETURN)) BALL_YPOS BACKGROUNDRIGHT)))

(animate to-and-fro)
   
  