# kixi.anonymisation

[![Build Status](https://travis-ci.org/MastodonC/kixi.anonymisation.svg?branch=master)](https://travis-ci.org/MastodonC/kixi.anonymisation)

Anonymisation and recovery of text content.

Converts words into hashes while preserving sentence structure (which might be useful for further text processing). Supports recovery of hashed content through a lookup, though the recovered content will be stemmed. Removing words from the lookup means some words will never be recovered from their hash.

## Usage

### Hiding

```clojure
(require '[kixi.anonymisation.hide :as hide])

(hide/from-chunk "Sometimes I believe in as many as six impossible things before breakfast.")
;;=>
;; {:lookup {thing     d108c3ee3dcb2d15f774d1d1b93f77b8dd4aee21e45261ea0f47fdaaee645ee4,
;;           six       8c494b0091d355d522ecf3bc618e6f462007e53312fe0b9ff22c2535c83486cd,
;;           sometim   642581c01fff119363e263bced731931c72146d1ec54a55870d0369ff31e7d96,
;;           imposs    220392cbd6fa1b4af4463519f238f734c220130b422a316fb9b2dad7280a75c4,
;;           i         b11b13b16d4ec5587826235afa38cd435f18b99f99294352f9aff606c2f13bf2,
;;           befor     20370335fe238fd7f0e209c1bd2785076d105a588fe697a5316f5f834e07bb2c,
;;           mani      b5861a37dc467bb5f5295b76722a599fae33fa2b2ef1fe457b647afe592182d2,
;;           believ    f4ab0270f7b4038b1432ecd30a50488ed202ae241d2d8c1b104cea14162ebaff,
;;           as        18b4542dbd67bd0f1dc5fafd99ba5b3f7702c70395cd50ef187ce2ceccc09dad,
;;           in        7185fc4877d4151d79e7c8e63d94bfcafb11fc28a5a4424301b0410533089ff1,
;;           breakfast ca080ed2e7e5174d670e96313de7c6caf2d9beca568451eda854184a4f583a3c},
;;
;;  :content
;;           642581c01fff119363e263bced731931c72146d1ec54a55870d0369ff31e7d96 b11b13b16d4ec5587826235afa38cd435f18b99f99294352f9aff606c2f13bf2 f4ab0270f7b4038b1432ecd30a50488ed202ae241d2d8c1b104cea14162ebaff 7185fc4877d4151d79e7c8e63d94bfcafb11fc28a5a4424301b0410533089ff1 18b4542dbd67bd0f1dc5fafd99ba5b3f7702c70395cd50ef187ce2ceccc09dad b5861a37dc467bb5f5295b76722a599fae33fa2b2ef1fe457b647afe592182d2 18b4542dbd67bd0f1dc5fafd99ba5b3f7702c70395cd50ef187ce2ceccc09dad 8c494b0091d355d522ecf3bc618e6f462007e53312fe0b9ff22c2535c83486cd 220392cbd6fa1b4af4463519f238f734c220130b422a316fb9b2dad7280a75c4 d108c3ee3dcb2d15f774d1d1b93f77b8dd4aee21e45261ea0f47fdaaee645ee4 20370335fe238fd7f0e209c1bd2785076d105a588fe697a5316f5f834e07bb2c ca080ed2e7e5174d670e96313de7c6caf2d9beca568451eda854184a4f583a3c.
;;}
```

#### Working with files:

```clojure
(hide/from-file "in-directory/input.txt" "out-directory/out.txt" "whitelist.txt")
;;lookup.edn is written to disk in `out-directory`
;;lookup.edn.whitelisted is written to disk in `out-directory`
```

whitelist.txt is a list of words delimited by newlines.

```
impossible
breakfast
six
```


### Recovering

```clojure
(require '[kixi.anonymisation.recover :as recover])

;;Assuming lookup and content from hiding example:
(recover/from-chunk lookup content)
;;=>
;; "sometim i believ in as mani as six imposs thing befor breakfast."
```

#### Working with files:

```clojure
(recover/from-file "dir/lookup.edn" "dir/out.txt")
;;dir/out.txt.recovered is written to disk with recovered text.
```

## Limitations

* Assumes the hash to word lookup will fit in memory.
* Anonimisation of a file is processed in batches of lines. It is assumed these lines will fit in memory.

## License

Copyright © 2017 MastodonC

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
