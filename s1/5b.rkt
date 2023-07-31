;; The first three lines of this file were inserted by DrRacket. They record metadata
;; about the language level of this file in a form that our tools can easily process.
#reader(lib "htdp-beginner-abbr-reader.ss" "lang")((modname hw5b) (read-case-sensitive #t) (teachpacks ()) (htdp-settings #(#t constructor repeating-decimal #f #t none #f () #f)))
; An LoN (List of Numbers) is one of:
; - '()
; - (cons Number LoN)
; An LoN represents multiple numbers in a List
; List Examples
(define l1 (list 10 20 30))
(define l2 (list 1 2 3))
(define l3 (list 4 5 6 7 8 9))
; LoN-temp : LoN -> ???
#;(define (lon-temp lon)
    (cond
      [(empty? lon) ...]
      [(cons? lon) (... (first lon) ... (lon-temp (rest lon)))]))

; interleave : List List -> List
; Takes two lists and creates one with alternating elements
; from the original lists
; If one is longer, appends remainder of longer list to new list
; Begins new list with element of first inputted list
(define (interleave l1 l2)
  (cond
    [(empty? l2) l1]
    [(empty? l1) l2]
    [(cons? l1) (cons (first l1) (cons (first l2) (interleave (rest l1) (rest l2))))]))
; Tests
(check-expect (interleave l1 l2) (list 10 1 20 2 30 3))
(check-expect (interleave l1 l3) (list 10 4 20 5 30 6 7 8 9))
(check-expect (interleave l3 l2) (list 4 1 5 2 6 3 7 8 9))

;====================================================================================================

; a SubList is a List of LoN
; a SubList is a List of lists whose items appear in the same relative order that they
; do in the initial list and all of the items appear in the initial list
; for non-empty sets, an element/Number can either be or not be included in a sublist (2^n total)  
; SubList Examples
; SubList of l1
(define sl1 (list
             (list 10 20 30)
             (list 20 30)
             (list 10 30)
             (list 30)
             (list 10 20)
             (list 20)
             (list 10)
             '()))
; SubList of l2
(define sl2 (list
             (list 1 2 3)
             (list 2 3)
             (list 1 3)
             (list 3)
             (list 1 2)
             (list 2)
             (list 1)
             '()))
; SubList-temp : SubList -> ???
#;
(define (subList-temp sl)
  (cond
    [(empty? sl) ...]
    [(cons? sl) (... (lon-temp (first sl)) ... (sublist-temp (rest sl)))]))

; powerlist : List -> SubList
; Given a list with no duplicates, creates a List with all the SubLists
(define (powerlist l)
  (cond
    [(empty? l) (list empty)]
    [(cons? l) (determine-sublists-element (powerlist (rest l)) (first l))]))
; Tests
(check-expect (powerlist l1) sl1)
(check-expect (powerlist l2) sl2)

; determine-sublists-element: List Number -> Sublist
; determines all sublists from the *** recursive list *** which include the given number
(define (determine-sublists-element l n)
  (cond
    [(empty? l) empty]
    [(cons? l) (cons
                (cons n (first l))
                (cons (first l)
                      (determine-sublists-element (rest l) n)))]))
(check-expect (determine-sublists-element (list (list 20 30) (list 20) (list 30)) 10)
              (list
               (list 10 20 30)
               (list 20 30)
               (list 10 20)
               (list 20)
               (list 10 30)
               (list 30)))
(check-expect (determine-sublists-element (list (list 30)) 20)
              (list
               (list 20 30)
               (list 30)))

;====================================================================================================

; An LoLoN (List of List of Numbers) is one of:
; - (cons LoN empty)
; - (cons LoN LoLoN)
; LoLoN Examples
(define lolon-1 (list (list 10 20) (list 20 30)))
(define lolon-2 (list (list 10 20 30 40 50) (list 40 50) (list 30 40 50)))
(define lolon-3 (list (list 50 60 70) (list 100 120 140)))
(define lolon-4 (list (list 1000 10000 100000 1000000)))
; lolon-temp : LoLon -> LoN
#;
(define (lolon-temp lolon)
  (cond
    [(empty? lolon) ...]
    [(cons? lolon) ... (lon-temp (first lolon)) ...
                   ... (lolon-temp (rest lolon)) ...]))

; intersection : LoLoN -> LoN
; Given a List of LoN, returns the numbers that appear in every sublist
; Assume sublists do not repeat numbers
(define (intersection lolon)
  (find-within (first lolon) (rest lolon))) ; if number isn't in first list, its not returned
;                                             therefore, we only have to check first list
; Tests
(check-expect (intersection lolon-1) (list 20))
(check-expect (intersection lolon-2) (list 40 50))
(check-expect (intersection lolon-3) empty)
(check-expect (intersection lolon-4) (list 1000 10000 100000 1000000))

; find-within : LoN LoLoN -> LoN
; Returns a list of all of the numbers in LoN that repeat in every list in the LoLoN
(define (find-within lon lolon)
  (cond
    [(empty? lolon) lon] ; this means the first LoN was the only LoN in the LoLoN, we can just return
    [(empty? lon) empty]
    [(cons? lolon) (if (equal? (check-in-all? (first lon) lolon) #true)
                       (cons (first lon)
                             (find-within (rest lon) lolon))
                       (find-within (rest lon) lolon))]))
; Tests
(check-expect (find-within (list 5 10 50) lolon-2) (list 50))
(check-expect (find-within (list 5 10 15) lolon-2) empty)
(check-expect (find-within (list 5 10 15) empty) (list 5 10 15))
(check-expect (find-within empty lolon-3) empty)

; check-in-all?: Number LoLoN -> Boolean
; returns true number is in every list in the LoLoN
(define (check-in-all? num lolon)
  (cond
    [(empty? lolon) #true] ; we can do this because lolon is nonempty, this considers recursion 
    [(cons? lolon) (and (num-in-list? num (first lolon)) (check-in-all? num (rest lolon)))]))
; Tests
(check-expect (check-in-all? 50 lolon-2) #true)
(check-expect (check-in-all? 50 empty) #true)

; num-in-list? : num LoN -> Boolean
; returns true if number is in the LoN
(define (num-in-list? num lon)
  (cond
    [(empty? lon) #false]
    [(cons? lon) (or (= num (first lon))
                     (num-in-list? num (rest lon)))]))
; Tests
(check-expect (num-in-list? 15 (list 5 10 15)) #true)
(check-expect (num-in-list? 10 empty) #false)
