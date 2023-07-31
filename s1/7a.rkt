;; The first three lines of this file were inserted by DrRacket. They record metadata
;; about the language level of this file in a form that our tools can easily process.
#reader(lib "htdp-intermediate-reader.ss" "lang")((modname hw7a) (read-case-sensitive #t) (teachpacks ()) (htdp-settings #(#t constructor repeating-decimal #f #t none #f () #f)))
;; A [Keyed X] is a (list Number X)
;; and represents an X and its extracted "key" 

;; A Movie is a (make-movie String Number)
(define-struct movie [title runtime])
;; and represents a movie's title and runtime in minutes

;; sort-list-numerically : [List-of X] [[List-of X] -> Number] -> [List-of X]
;; Sorts a list by comparing traits that can be expressed numberically (ascending)
(define (sort-list-numerically lox func)
  (local [;; insert-by-trait : [Keyed X] [List-of [Keyed X]] -> [List-of [Keyed X]]
          (define (insert-by-trait kx lokx)
            (cond [(empty? lokx) (list kx)]
                  [(cons? lokx)
                   (if (<= (first kx) (first (first lokx)))
                       (cons kx lokx)
                       (cons (first lokx) (insert-by-trait kx (rest lokx))))]))]
    (map second
         (foldr insert-by-trait
                '()
                (map list (map func lox) lox)))))
;; Test- Sorts a [List-of [List-of Number]] by how many elements are in its sublist (ascending)
(check-expect (sort-list-numerically (list (list 1 2 3) (list 4 5) (list 0 -9 2 5 1) '()) length)
              (list '() (list 4 5) (list 1 2 3) (list 0 -9 2 5 1)))


;; sort-by-title-length-update : [List-of Movie] -> [List-of Movie]
;; Sort the movies by their title's length (ascending)
(define (sort-by-title-length-update lom)
  (sort-list-numerically lom (compose string-length movie-title)))
;; Test
(check-expect (sort-by-title-length-update
               (list (make-movie "Sorry To Bother You" 111)
                     (make-movie "Hereditary" 127)
                     (make-movie "Annihilation" 120)
                     (make-movie "Blindspotting" 96)
                     (make-movie "You Were Never Really Here" 95)))
              (list
               (make-movie "Hereditary" 127)
               (make-movie "Annihilation" 120)
               (make-movie "Blindspotting" 96)
               (make-movie "Sorry To Bother You" 111)
               (make-movie "You Were Never Really Here" 95)))

;; sort-by-biggest-update : [List-of [NEList-of Number]] -> [List-of [NEList-of Number]]
;; Sort the lists by their biggest element (ascending)
(define (sort-by-biggest-update lonelon)
  (local [;; biggest : [NEList-of Number] -> Number
          ;; Find the biggest number in the non-empty list of numbers
          (define (biggest nelon)
            (foldr max (first nelon) (rest nelon)))]
    (sort-list-numerically lonelon biggest)))
;; Test
(check-expect (sort-by-biggest-update (list (list 6) (list 1 2 3) (list 5 6) (list 23)))
              (list (list 1 2 3) (list 6) (list 5 6) (list 23)))

;; sort-by-title-length : [List-of Movie] -> [List-of Movie]
;; Sort the movies by their title's length (ascending)
(define (sort-by-title-length lom)
  (local [;; insert-by-title-length : [Keyed Movie] [List-of [Keyed Movie]] -> [List-of [Keyed Movie]]
          ;; Find the correct spot for the title length
          (define (insert-by-title-length nm lonm)
            (cond [(empty? lonm) (list nm)]
                  [(cons? lonm)
                   (if (<= (first nm) (first (first lonm)))
                       (cons nm lonm)
                       (cons (first lonm) (insert-by-title-length nm (rest lonm))))]))]
    (map second
         (foldr insert-by-title-length
                '()
                (map list (map (compose string-length movie-title) lom) lom)))))
;; Test
(check-expect (sort-by-title-length
               (list (make-movie "Sorry To Bother You" 111)
                     (make-movie "Hereditary" 127)
                     (make-movie "Annihilation" 120)
                     (make-movie "Blindspotting" 96)
                     (make-movie "You Were Never Really Here" 95)))
              (list
               (make-movie "Hereditary" 127)
               (make-movie "Annihilation" 120)
               (make-movie "Blindspotting" 96)
               (make-movie "Sorry To Bother You" 111)
               (make-movie "You Were Never Really Here" 95)))

;; sort-by-biggest : [List-of [NEList-of Number]] -> [List-of [NEList-of Number]]
;; Sort the lists by their biggest element (ascending)
(define (sort-by-biggest lonelon)
  (local [;; A KNELoN is a [Keyed [NEList-of Number]]]
          ;; insert-by-biggest : KNELoN [List-of KNELoN] -> [List-of KNELoN]
          ;; Find the correct spot for the biggest number
          (define (insert-by-biggest nnelon lonnelon)
            (cond [(empty? lonnelon) (list nnelon)]
                  [(cons? lonnelon)
                   (if (<= (first nnelon) (first (first lonnelon)))
                       (cons nnelon lonnelon)
                       (cons (first lonnelon) (insert-by-biggest nnelon (rest lonnelon))))]))
          ;; biggest : [NEList-of Number] -> Number
          ;; Find the biggest number in the non-empty list of numbers
          (define (biggest nelon)
            (foldr max (first nelon) (rest nelon)))]
    (map second (foldr insert-by-biggest '() (map list (map biggest lonelon) lonelon)))))
;; Test
(check-expect (sort-by-biggest (list (list 6) (list 1 2 3) (list 5 6) (list 23)))
              (list (list 1 2 3) (list 6) (list 5 6) (list 23)))

;====================================================================================================

;; dup-even-strings : [List-of String] -> [List-of String]
;; Given a list of strings, returns the same list but with two copies
;; (next to each other in the list, as seperate strings)
;; of all strings with an even string-length.
(define (dup-even-strings los)
  (local [;; create-even-dup-list : String [List-of String] -> [List-of String]
          ;; Creates a list with a duplicated String if the String length is even
          ;; otherwise creates a list with a single copy of the String
          (define (create-even-dup-list str rest)
            (if (even? (string-length str)) (cons str (cons str rest)) (cons str rest)))]
    (foldr create-even-dup-list '() los)))
;; Tests
(check-expect (dup-even-strings (list)) (list))
(check-expect (dup-even-strings (list "a" "bc" "" "def"))
              (list "a" "bc" "bc" "" "" "def"))
(check-expect (dup-even-strings (list "berry" "mango"))
              (list "berry" "mango"))
(check-expect (dup-even-strings (list "berry" "mango" "mango"))
              (list "berry" "mango" "mango"))
(check-expect (dup-even-strings (list "strawberry" "cheesecake"))
              (list "strawberry" "strawberry" "cheesecake" "cheesecake"))
(check-expect (dup-even-strings (list "strawberry" "cheesecake"  "cheesecake"))
              (list "strawberry" "strawberry" "cheesecake" "cheesecake"  "cheesecake"  "cheesecake"))

;====================================================================================================

;; scalar-matrix : Natural Number -> [List-of [List-of Number]]
;; Given a natural number n and a number k, outputs an nxn matrix (list of list of numbers)
;; where the diagonal entries are k and the other elements are 0.
(define (scalar-matrix n k)
  (local [;; create-row : Number -> [List-of Number]
          ;; Creates a list of numbers of length n so that it contains k in its xth position
          ;; indexing begins at 0
          (define (create-row x)
            (local [(define begin-row (make-list x 0)) ;; Part of list until k expected (all 0's)
                    (define end-row (make-list (- n x 1) 0))] ;; Rest of list after k (all 0's)
              (append begin-row (list k) end-row)))] ;; Places k in between the beginning and end 0's
    (build-list n create-row)))
;; Tests
(check-expect (scalar-matrix 0 1) (list))
(check-expect (scalar-matrix 2 5)
              (list (list 5 0) (list 0 5)))
(check-expect (scalar-matrix 1 -1)
              (list (list -1)))
(check-expect (scalar-matrix 5 2)
              (list (list 2 0 0 0 0)
                    (list 0 2 0 0 0)
                    (list 0 0 2 0 0)
                    (list 0 0 0 2 0)
                    (list 0 0 0 0 2)))

;=====================================================================================================

; A Network is a [List-of Person]
 
; A Person is a (make-person String Pet [List-of String])
(define-struct person [name pet friends])
; and represents their name, their type of pet, and the name of their friends
; assume all of the names in the network are unique, and that the names of friends are unique
; and represent actual people in the network
 
; A Pet is one of:
; - "dog"
; - "cat"
 
(define NETWORK
  (list
   (make-person "Alice" "dog" (list "Carol" "Heidi"))
   (make-person "Bob" "cat" (list "Carol" "Dan"))
   (make-person "Carol" "dog" (list "Alice"))
   (make-person "Dan" "cat" (list "Carol" "Eric" "Frank" "Grace"))
   (make-person "Eric" "dog" (list "Alice" "Bob" "Carol" "Dan" "Frank" "Grace"))
   (make-person "Frank" "cat" (list "Alice" "Bob" "Carol" "Dan" "Grace"))
   (make-person "Grace" "dog" (list "Bob" "Frank"))
   (make-person "Heidi" "cat" (list "Alice" "Bob" "Carol" "Dan" "Eric" "Grace"))))
 
; get-person : Network String -> Person
; Get the person in the network (assume they are there)
(define (get-person n s)
  (cond [(string=? s (person-name (first n))) (first n)]
        [else (get-person (rest n) s)]))
(check-expect (get-person NETWORK "Bob") (make-person "Bob" "cat" (list "Carol" "Dan")))

; update-network : Network -> Network
; updates everyone's pet to the majority pet if any
(define (update-network n)
  (local [; update-majority : Person -> Person
          (define (update-majority p)
            (if (not (string=? (find-majority n) ""))
                (make-person (person-name p)
                             (find-majority n)
                             (person-friends p))
                p))
          ; find-majority : Network -> String
          ; finds the most common pet if any
          (define (find-majority n)
            (cond [(= (length (check-dog n)) (/ (length n) 2)) ""]
                  [(> (length (check-dog n)) (/ (length n) 2)) "dog"]
                  [(< (length (check-dog n)) (/ (length n) 2)) "cat"]))
          ; check-dog : Network -> Network
          ; returns network of people who have a dog
          (define (check-dog n)
            (filter equals-dog n))
          ; equals-dog : Person -> Boolean
          ; checks if persons pet is a dog
          (define (equals-dog p)
            (string=? "dog" (person-pet p)))
          ]
    (map update-majority n)))
(check-expect (update-network NETWORK)
              (list
               (make-person "Alice" "dog" (list "Carol" "Heidi"))
               (make-person "Bob" "cat" (list "Carol" "Dan"))
               (make-person "Carol" "dog" (list "Alice"))
               (make-person "Dan" "cat" (list "Carol" "Eric" "Frank" "Grace"))
               (make-person "Eric" "dog" (list "Alice" "Bob" "Carol" "Dan" "Frank" "Grace"))
               (make-person "Frank" "cat" (list "Alice" "Bob" "Carol" "Dan" "Grace"))
               (make-person "Grace" "dog" (list "Bob" "Frank"))
               (make-person "Heidi" "cat" (list "Alice" "Bob" "Carol" "Dan" "Eric" "Grace"))))
(check-expect (update-network
               (list
                (make-person "Alice" "dog" (list "Carol" "Heidi"))
                (make-person "Bob" "dog" (list "Carol" "Dan"))
                (make-person "Carol" "dog" (list "Alice"))
                (make-person "Dan" "cat" (list "Carol" "Eric" "Frank" "Grace"))
                (make-person "Eric" "dog" (list "Alice" "Bob" "Carol" "Dan" "Frank" "Grace"))
                (make-person "Frank" "cat" (list "Alice" "Bob" "Carol" "Dan" "Grace"))
                (make-person "Grace" "dog" (list "Bob" "Frank"))
                (make-person "Heidi" "cat" (list "Alice" "Bob" "Carol" "Dan" "Eric" "Grace"))))
              (list
               (make-person "Alice" "dog" (list "Carol" "Heidi"))
               (make-person "Bob" "dog" (list "Carol" "Dan"))
               (make-person "Carol" "dog" (list "Alice"))
               (make-person "Dan" "dog" (list "Carol" "Eric" "Frank" "Grace"))
               (make-person "Eric" "dog" (list "Alice" "Bob" "Carol" "Dan" "Frank" "Grace"))
               (make-person "Frank" "dog" (list "Alice" "Bob" "Carol" "Dan" "Grace"))
               (make-person "Grace" "dog" (list "Bob" "Frank"))
               (make-person "Heidi" "dog" (list "Alice" "Bob" "Carol" "Dan" "Eric" "Grace"))))
(check-expect (update-network
               (list
                (make-person "Alice" "dog" (list "Carol" "Heidi"))
                (make-person "Bob" "cat" (list "Carol" "Dan"))
                (make-person "Carol" "dog" (list "Alice"))
                (make-person "Dan" "cat" (list "Carol" "Eric" "Frank" "Grace"))
                (make-person "Eric" "dog" (list "Alice" "Bob" "Carol" "Dan" "Frank" "Grace"))
                (make-person "Frank" "cat" (list "Alice" "Bob" "Carol" "Dan" "Grace"))
                (make-person "Grace" "cat" (list "Bob" "Frank"))
                (make-person "Heidi" "cat" (list "Alice" "Bob" "Carol" "Dan" "Eric" "Grace"))))
              (list
               (make-person "Alice" "cat" (list "Carol" "Heidi"))
               (make-person "Bob" "cat" (list "Carol" "Dan"))
               (make-person "Carol" "cat" (list "Alice"))
               (make-person "Dan" "cat" (list "Carol" "Eric" "Frank" "Grace"))
               (make-person "Eric" "cat" (list "Alice" "Bob" "Carol" "Dan" "Frank" "Grace"))
               (make-person "Frank" "cat" (list "Alice" "Bob" "Carol" "Dan" "Grace"))
               (make-person "Grace" "cat" (list "Bob" "Frank"))
               (make-person "Heidi" "cat" (list "Alice" "Bob" "Carol" "Dan" "Eric" "Grace"))))

; bubbled? : String Network -> Boolean
; determines if a person is only friends with people who have their same pet
(define (bubbled? name n)
  (local [; same-pet : String -> Boolean
          ; checks if pet of someone is the same as the person whose friends we are checking 
          (define (same-pet friend)
            (string=? (person-pet (get-person n name))
                      (person-pet (get-person n friend))))]
    (andmap same-pet (person-friends (get-person n name)))))

(check-expect (bubbled? "Carol" NETWORK) #true)
(check-expect (bubbled? "Alice" NETWORK) #false)
(check-expect (bubbled? "Dan" NETWORK) #false)

; bubble : Pet Network -> Network
; removes everyone from a network who doesn't have a certain pet
(define (bubble pet n)
  (local [; is-in-network? : Name Network -> Boolean
          ; sees if person with name is in network
          (define (is-in-network? name n)
            (cond [(empty? n) #false]
                  [(cons? n) (or
                              (string=? name (person-name(first n)))
                              (is-in-network? name (rest n)))]))
          ; has-pet : Person -> Boolean
          ; returns true if person has specified pet
          (define (has-pet p)
            (string=? pet (person-pet p)))
          ; update-friends : Person -> Person
          ; removes friends who dont have the specified pet
          (define (update-friends p)
            (make-person (person-name p)
                         (person-pet p)
                         (foldr remove-friends '() (person-friends p))))
          ; remove-friends : [List-of Person] -> [List-of Person]
          ; returns all friends who have specified pet
          (define (remove-friends name l)
            (if (is-in-network? name (filter has-pet n))
                (cons name l)
                l))]
    (map update-friends (filter has-pet n))))
(check-expect (bubble "dog" NETWORK)
              (list
               (make-person "Alice" "dog" (list "Carol"))
               (make-person "Carol" "dog" (list "Alice"))
               (make-person "Eric" "dog" (list "Alice" "Carol" "Grace"))
               (make-person "Grace" "dog" empty)))
(check-expect (bubble "cat" NETWORK)
              (list
               (make-person "Bob" "cat" (list "Dan"))
               (make-person "Dan" "cat" (list "Frank"))
               (make-person "Frank" "cat" (list "Bob" "Dan"))
               (make-person "Heidi" "cat" (list "Bob" "Dan"))))
(check-expect (bubble "dog" (list
                             (make-person "Bob" "cat" (list "Dan"))
                             (make-person "Dan" "cat" (list "Frank"))
                             (make-person "Frank" "cat" (list "Bob" "Dan"))
                             (make-person "Heidi" "cat" (list "Bob" "Dan")))) '())
(check-expect (bubble "cat" (list
                             (make-person "Bob" "dog" (list "Dan"))
                             (make-person "Dan" "dog" (list "Frank"))
                             (make-person "Frank" "dog" (list "Bob" "Dan"))
                             (make-person "Heidi" "dog" (list "Bob" "Dan")))) '())
(check-expect (bubble "dog"
                      (list
                       (make-person "Alice" "dog" (list "Carol" "Heidi"))
                       (make-person "Bob" "dog" (list "Carol" "Dan"))
                       (make-person "Carol" "dog" (list "Alice"))
                       (make-person "Dan" "dog" (list "Carol" "Eric" "Frank" "Grace"))
                       (make-person "Eric" "dog" (list "Alice" "Bob" "Carol" "Dan" "Frank" "Grace"))
                       (make-person "Frank" "dog" (list "Alice" "Bob" "Carol" "Dan" "Grace"))
                       (make-person "Grace" "dog" (list "Bob" "Frank"))
                       (make-person "Heidi" "dog" (list "Alice" "Bob" "Carol" "Dan" "Eric" "Grace"))))
              (list
               (make-person "Alice" "dog" (list "Carol" "Heidi"))
               (make-person "Bob" "dog" (list "Carol" "Dan"))
               (make-person "Carol" "dog" (list "Alice"))
               (make-person "Dan" "dog" (list "Carol" "Eric" "Frank" "Grace"))
               (make-person "Eric" "dog" (list "Alice" "Bob" "Carol" "Dan" "Frank" "Grace"))
               (make-person "Frank" "dog" (list "Alice" "Bob" "Carol" "Dan" "Grace"))
               (make-person "Grace" "dog" (list "Bob" "Frank"))
               (make-person "Heidi" "dog" (list "Alice" "Bob" "Carol" "Dan" "Eric" "Grace"))))
(check-expect (bubble "cat"
                      (list
                       (make-person "Alice" "cat" (list "Carol" "Heidi"))
                       (make-person "Bob" "cat" (list "Carol" "Dan"))
                       (make-person "Carol" "cat" (list "Alice"))
                       (make-person "Dan" "cat" (list "Carol" "Eric" "Frank" "Grace"))
                       (make-person "Eric" "cat" (list "Alice" "Bob" "Carol" "Dan" "Frank" "Grace"))
                       (make-person "Frank" "cat" (list "Alice" "Bob" "Carol" "Dan" "Grace"))
                       (make-person "Grace" "cat" (list "Bob" "Frank"))
                       (make-person "Heidi" "cat" (list "Alice" "Bob" "Carol" "Dan" "Eric" "Grace"))))
              (list
               (make-person "Alice" "cat" (list "Carol" "Heidi"))
               (make-person "Bob" "cat" (list "Carol" "Dan"))
               (make-person "Carol" "cat" (list "Alice"))
               (make-person "Dan" "cat" (list "Carol" "Eric" "Frank" "Grace"))
               (make-person "Eric" "cat" (list "Alice" "Bob" "Carol" "Dan" "Frank" "Grace"))
               (make-person "Frank" "cat" (list "Alice" "Bob" "Carol" "Dan" "Grace"))
               (make-person "Grace" "cat" (list "Bob" "Frank"))
               (make-person "Heidi" "cat" (list "Alice" "Bob" "Carol" "Dan" "Eric" "Grace"))))
