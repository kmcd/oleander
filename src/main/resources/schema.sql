create table spy_ask(time_stamp timestamp primary key, price decimal not null );
create table spy_bid(time_stamp timestamp primary key, price decimal not null );
create table ivv_bid(time_stamp timestamp primary key, price decimal not null );
create table ivv_ask(time_stamp timestamp primary key, price decimal not null );
create table long_spreads(time_stamp timestamp primary key, spread decimal not null);
create table short_spreads(time_stamp timestamp primary key, spread decimal not null);

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
