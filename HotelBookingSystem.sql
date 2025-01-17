create database HotelBookingSystem;

use HotelBookingSystem;

create table Rooms (room_id int primary key, room_number varchar(10), room_type varchar(20), price decimal(10,2), availability boolean);

create table Guests (guest_id int primary key, name varchar(255), contact_info varchar(255), id_proof varchar(255));

create table Booking (booking_id int primary key, room_id int, guest_id int, check_in_date date, check_out_date date,
foreign key (room_id) references Rooms(room_id),
foreign key (guest_id) references Guests(guest_id)
);

insert into Rooms (room_id,room_number,room_type,price,availability)
values(1,'hi','bye',0,true);

select * from Rooms;