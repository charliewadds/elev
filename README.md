




## UDP Protocol
the protocols

### Elevator Commands

#### <u>Go to floor</u>
- 0b00000000(0x00) Command code
- 0bxxxxxxxx(0xXX) floor number
#### <u>Open and close door</u>
- 0b00000001 Command code
- 0b00000000(0x00) Open or 0b00000001(0x01) Close
#### <u>Request floor / press elevator button</u>
- 0b00000010 Command code
- 0bxxxxxxxx(0xXX) floor number


## Scheduler Commands

#### <u>up/down button pressed</u>
- 0b00000000(0x00) Command code
- 0b00000000(0x00) Up or 0b00000001(0x01) Down
- 0bxxxxxxxx(0xXX) floor number

#### <u>Elevator reached floor</u>
- 0b00000001 Command code
- 0bxxxxxxxx(0xXX) floor number

#### <u>Elevator floor request button pressed</u>
- 0b00000010 Command code
- 0bxxxxxxxx(0xXX) floor number
- 0bxxxxxxxx(0xXX) elevator number

