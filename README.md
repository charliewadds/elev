




## UDP Protocol
the protocols


# Floor
## recv
- elevator arrived: 0x00
- push button: 0x01
  - 0x01: up
  - 0x02: down
## send
- floor button
  - 0x01: up
  - 0x02: down


# Scheduler
- ## recv
  - 0x00: floor button
    - ### byte 1
      - 0x00: up
      - 0x01: down
    - ### byte 2
      - floor number
  - 