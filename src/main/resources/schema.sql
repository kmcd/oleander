create table spy_bid(
   time_stamp timestamp,
   price decimal not null,
   
   primary key(time_stamp)
);

create table spy_ask(
   time_stamp timestamp,
   price decimal not null,
   
   primary key(time_stamp)
);

create table ivv_bid(
   time_stamp timestamp,
   price decimal not null,
   
   primary key(time_stamp)
);

create table ivv_ask(
   time_stamp timestamp,
   price decimal not null,
   
   primary key(time_stamp)
);

create view long_spreads as
   select spy_ask.time_stamp, spy_ask.price - ivv_bid.price as spread
   from spy_ask, ivv_bid
   where spy_ask.time_stamp = ivv_bid.time_stamp;

create view short_spreads as
   select spy_bid.time_stamp, spy_bid.price - ivv_ask.price as spread
   from spy_bid, ivv_ask
   where spy_bid.time_stamp = ivv_ask.time_stamp;

create function current_long_spread() returns decimal as
   'select spread
   from long_spreads
   where time_stamp >= current_date
   order by time_stamp desc
   limit 1'
   language sql;
   
create function current_short_spread() returns decimal as
   'select spread
   from short_spreads
   where time_stamp >= current_date
   order by time_stamp desc
   limit 1'
   language sql;

create function long_entry_signal() returns numeric as
   'select avg(spread) - current_long_spread()
   as entry_signal
   from long_spreads'
   language sql;
   
create function short_entry_signal() returns numeric as
   'select avg(spread) - current_short_spread()
   as entry_signal
   from short_spreads'
   language sql;
   
create view long_signals as
   select ls.time_stamp, ls.spread, sa.price as spy_ask, ib.price as ivv_bid, avg(ls.spread)
   over (order by ls.time_stamp)
   from long_spreads ls, spy_ask sa, ivv_bid ib
   where sa.time_stamp = ls.time_stamp 
   and ib.time_stamp = ls.time_stamp;
   
create view long_entry_signals as
   select time_stamp, avg - spread as spread, spy_ask, ivv_bid 
   from long_signals;
   
create view short_signals as
   select ss.time_stamp, ss.spread, sb.price as spy_bid, ia.price as ivv_ask, avg(ss.spread)
   over (order by ss.time_stamp)
   from short_spreads ss, spy_bid sb, ivv_ask ia
   where ss.time_stamp = sb.time_stamp 
   and ss.time_stamp = ia.time_stamp;
   
create view short_entry_signals as
   select time_stamp, avg - spread as spread, spy_bid, ivv_ask
   from short_signals;
