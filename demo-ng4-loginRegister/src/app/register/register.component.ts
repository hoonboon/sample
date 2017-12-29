import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { LoggerService } from '../common/services/logger.service';
import { AlertService } from '../common/services/alert.service';
import { UserService } from '../common/services/user.service';

@Component({
  moduleId: module.id,
  templateUrl: 'register.component.html'
})
export class RegisterComponent {
  
  model: any = {};
  loading = false;
  
  constructor(
      private logger: LoggerService,
      private alertService: AlertService,
      private userService: UserService,
      private router: Router) { }
  
  register() {
    this.logger.info("in register()");
    this.logger.info("model: " + this.diagnostic);
    
    this.loading = true;
    this.userService.create(this.model)
      .subscribe(
        data => {
          this.alertService.success('Registration successful', true);
          this.router.navigate(['/login']);
        },
        error => {
          this.alertService.error(error);
          this.loading = false;
        });
  }
  
  // TODO: remove this when development done
  get diagnostic() {
    return JSON.stringify(this.model);
  }
}
