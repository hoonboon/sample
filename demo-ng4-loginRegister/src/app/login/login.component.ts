import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { AlertService } from '../common/services/alert.service';
import { LoggerService } from '../common/services/logger.service';
import { AuthenticateService } from '../common/services/authenticate.service';

@Component({
  moduleId: module.id,
  templateUrl: 'login.component.html'
})
export class LoginComponent implements OnInit {
  model: any = {};
  loading = false;
  returnUrl: string;
  
  constructor(
      private route: ActivatedRoute,
      private router: Router,
      private alertService: AlertService,
      private logger: LoggerService,
      private authenticateService: AuthenticateService) { }
  
  ngOnInit() {
    // reset login status
    this.authenticateService.logout();
    
    // get return url from route parameters or default to '/'
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
  }
  
  login() {
    this.logger.info('login() start');
    this.loading = true;
    this.authenticateService.login(this.model.username, this.model.password)
      .subscribe(
          data => {
            this.router.navigate([this.returnUrl]);
          },
          error => {
            this.alertService.error(error);
            this.loading = false;
          });
  }
}
