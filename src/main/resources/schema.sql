create table spy_bid(time_stamp timestamp primary key, price decimal not null );
create table ivv_bid(time_stamp timestamp primary key, price decimal not null );
create table ivv_ask(time_stamp timestamp primary key, price decimal not null );
create table long_spreads(time_stamp timestamp primary key, spread decimal not null );
create table short_spreads(time_stamp timestamp primary key, spread decimal not null );

create function spy_bid(timestamp)
returns decimal as $$ 
select price from spy_bid where time_stamp <= $1 order by time_stamp  desc limit 1; 
$$ language sql;

create function spy_ask(timestamp)
returns decimal as $$ 
select price from spy_ask where time_stamp <= $1 order by time_stamp  desc limit 1; 
$$ language sql;

create function ivv_bid(timestamp)
returns decimal as $$ 
select price from ivv_bid where time_stamp <= $1 order by time_stamp  desc limit 1; 
$$ language sql;

create function ivv_ask(timestamp)
returns decimal as $$ 
select price from ivv_ask where time_stamp <= $1 order by time_stamp  desc limit 1; 
$$ language sql;

create rule calculate_long_spread_spy
as on insert to spy_ask
do insert into long_spreads values(NEW.time_stamp, NEW.price - ivv_bid(NEW.time_stamp));
   
create rule calculate_long_spread_ivv
as on insert to ivv_bid
do insert into long_spreads values(NEW.time_stamp, spy_ask(NEW.time_stamp) - NEW.price);
   
create rule calculate_short_spread_spy
as on insert to spy_bid
do insert into short_spreads values(NEW.time_stamp, NEW.price - ivv_ask(NEW.time_stamp));
   
create rule calculate_short_spread_ivv
as on insert to ivv_ask
do insert into short_spreads values(NEW.time_stamp, spy_bid(NEW.time_stamp) - NEW.price);

create or replace view short_entry_stats as
   select time_stamp, 
      spread - avg(spread) over( order by time_stamp ) as score,
      spread - stddev(spread) over( order by time_stamp ) as stddev
   from short_spreads;
  
create or replace view long_entry_stats as
select time_stamp, 
   spread - avg(spread) over( order by time_stamp ) as score,
   spread - stddev(spread) over( order by time_stamp ) as stddev
from long_spreads;
  
create view long_zscore as
   select time_stamp, score/stddev as zscore from long_entry_stats;
   
create view short_zscore as
   select time_stamp, score/stddev as zscore from short_entry_stats;
   
create view long_entry_signals as
   select time_stamp, zscore, spy_ask(time_stamp), ivv_bid(time_stamp)
   from long_zscore
   order by time_stamp desc; 
   
create view short_entry_signals as
   select time_stamp, zscore, spy_bid(time_stamp), ivv_ask(time_stamp)
   from short_zscore
   order by time_stamp desc; 
