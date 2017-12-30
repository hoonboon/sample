import { Component, OnInit } from '@angular/core';

import { AlertService } from '../common/services/alert.service';
import { LoggerService } from '../common/services/logger.service';
import { UserService } from '../common/services/user.service';

import { User } from '../common/models/user';

@Component({
  moduleId: module.id,
  templateUrl: 'home.component.html'
})
export class HomeComponent implements OnInit {
  
  currentUser: User;
  users: User[] = [];
  
  constructor (
      private alertService: AlertService,
      private logger: LoggerService,
      private userService: UserService) { 
    
    this.currentUser = JSON.parse(localStorage.getItem('currentUser'))
  }
  
  private loadAllUsers() {
    this.userService.getAll().subscribe(
        data => { 
          this.users = data;
        },
        error => {
          this.alertService.error(error);
        });
  }
  
  ngOnInit() {
    this.loadAllUsers();
  }
  
  deleteUser(id: number) {
    this.userService.delete(id).subscribe(
        data => {
          this.loadAllUsers();
        },
        error => {
          this.alertService.error(error);
        });
  }

}
