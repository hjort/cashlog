//
//  SimpleTableAppDelegate.m
//  SimpleTable
//
//  Created by Daniel Poit on 02/02/10.
//  Copyright __MyCompanyName__ 2010. All rights reserved.
//

#import "SimpleTableAppDelegate.h"
#import "SimpleTableViewController.h"

@implementation SimpleTableAppDelegate

@synthesize window;
@synthesize viewController;


- (void)applicationDidFinishLaunching:(UIApplication *)application {    
    		
		UINavigationController *navController = [[UINavigationController alloc] initWithRootViewController:viewController];
		[window addSubview:navController.view];
		[window makeKeyAndVisible];
		
}


- (void)dealloc {
    [viewController release];
    [window release];
    [super dealloc];
}


@end
